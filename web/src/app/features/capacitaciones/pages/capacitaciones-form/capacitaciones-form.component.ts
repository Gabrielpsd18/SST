import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { LucideCalendar, LucideClock, LucideUsers, LucideCheckCircle, LucidePlus, LucideLoader2, LucideBookOpen, LucideFileText } from '@lucide/angular';
import { of, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CapacitacionService } from '../../services/capacitacion.service';
import { TrabajadorService } from '../../../trabajadores/services/trabajador.service';
import { AuthService } from '../../../auth/services/auth.service';
import { SearchableSelectComponent, SearchableSelectOption } from '../../../../shared/components/ui/searchable-select/searchable-select.component';
import { CrearCapacitacionRequest } from '../../models/capacitacion.model';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-capacitaciones-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, SearchableSelectComponent],
  templateUrl: './capacitaciones-form.component.html',
  styleUrl: './capacitaciones-form.component.scss'
})
export class CapacitacionesFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly capacitacionService = inject(CapacitacionService);
  private readonly trabajadorService = inject(TrabajadorService);
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);

  protected readonly Calendar = LucideCalendar;
  protected readonly Clock = LucideClock;
  protected readonly Users = LucideUsers;
  protected readonly CheckCircle = LucideCheckCircle;
  protected readonly Plus = LucidePlus;
  protected readonly Loader2 = LucideLoader2;
  protected readonly BookOpen = LucideBookOpen;
  protected readonly FileText = LucideFileText;

  protected isAdmin = signal<boolean>(false);
  protected isEditMode = signal<boolean>(false);
  protected saving = signal<boolean>(false);
  protected errorMessage = signal<string>('');
  protected successMessage = signal<string>('');

  protected capacitacionForm!: FormGroup;
  
  protected sedes = signal<any[]>([]);
  protected trabajadores = signal<any[]>([]);
  protected capacitadores = signal<any[]>([]);

  // Pending trabajador IDs loaded from backend while trabajadores catalog may still be loading
  protected pendingSelectedTrabajadorIds = signal<number[]>([]);

  // For capacitador selector (fixed list) — map to SearchableSelectOption
  protected readonly searchableCapacitadorOptions = computed<SearchableSelectOption[]>(() =>
    this.capacitadores().map((c) => ({
      id: c.id,
      nombre: `${c.nombres} ${c.apellidos ?? ''} · ${c.empresa}`.trim()
    }))
  );

  // initial options to pass to the searchable-select for the capacitador(s)
  protected selectedCapacitadorInitial = signal<SearchableSelectOption[]>([]);
  // selected capacitadores (multiple)
  protected selectedCapacitadores = signal<SearchableSelectOption[]>([]);
  // Temporarily store capacitadorId(s) loaded from backend when editing, until capacitadores list is available
  protected editCapacitadorId = signal<number | number[] | null>(null);

  protected selectedTrabajadorIds = signal<number[]>([]);

  // Links: Videos and Forms
  protected videoLinks = signal<string[]>([]);
  protected formLinks = signal<string[]>([]);
  protected newVideoLink = signal<string>('');
  protected newFormLink = signal<string>('');

  protected addVideoLink(): void {
    const link = this.newVideoLink().trim();
    if (link.length > 0) {
      this.videoLinks.set([...this.videoLinks(), link]);
      this.newVideoLink.set('');
    }
  }

  protected removeVideoLink(index: number): void {
    this.videoLinks.set(this.videoLinks().filter((_, i) => i !== index));
  }

  protected addFormLink(): void {
    const link = this.newFormLink().trim();
    if (link.length > 0) {
      this.formLinks.set([...this.formLinks(), link]);
      this.newFormLink.set('');
    }
  }

  protected removeFormLink(index: number): void {
    this.formLinks.set(this.formLinks().filter((_, i) => i !== index));
  }

  // Map selected IDs to full trabajador objects for rendering
  protected readonly selectedTrabajadores = computed(() =>
    this.selectedTrabajadorIds()
      .map((id) => this.trabajadores().find((t) => t.id === id))
      .filter((t): t is any => Boolean(t))
  );

  protected totalAsignadosCount = computed(() => {
    if (this.selectedTrabajadorIds().length > 0) {
      return this.selectedTrabajadorIds().length;
    }
    return this.trabajadores().length;
  });

  protected addTrabajador(id: number): void {
    const next = Array.from(new Set([...this.selectedTrabajadorIds(), id]));
    this.selectedTrabajadorIds.set(next);
  }

  protected addAllFilteredTrabajadores(): void {
    const sedeFilter = this.capacitacionForm.value.sedeIdFilter;
    const filtered = this.trabajadores()
      .filter(tr => !sedeFilter || tr.sedeId === sedeFilter)
      .map(t => t.id);
    const combined = Array.from(new Set([...this.selectedTrabajadorIds(), ...filtered]));
    this.selectedTrabajadorIds.set(combined);
  }

  protected removeTrabajador(id: number): void {
    const next = this.selectedTrabajadorIds().filter((tid) => tid !== id);
    this.selectedTrabajadorIds.set(next);
  }

  protected isTrabajadorSelected(id: number): boolean {
    return this.selectedTrabajadorIds().includes(id);
  }

  // Responsables selector (multiple)
  protected responsablesSeleccionados = signal<SearchableSelectOption[]>([]);

  protected searchResponsables = (segment: string, limit: number): Observable<SearchableSelectOption[]> =>
  this.trabajadorService.searchTrabajadores(segment, limit, true).pipe(
      map((trabajadores) =>
        trabajadores.map((trabajador) => ({
          id: trabajador.id,
          nombre: `${trabajador.nombreCompleto || (trabajador.nombreCompleto)} · ${trabajador.cargoNombre ?? 'Sin cargo'} · ${trabajador.sedeNombre ?? 'Sin sede'}`
        }))
      )
    );

  protected onResponsablesChange(responsables: SearchableSelectOption[]): void {
    this.responsablesSeleccionados.set(responsables);
  }

  protected removeResponsable(id: number): void {
    const next = this.responsablesSeleccionados().filter(r => r.id !== id);
    this.responsablesSeleccionados.set(next);
  }

  // Search function for the capacitadores selector (filters local list)
  protected searchCapacitadores = (segment: string, limit: number): Observable<SearchableSelectOption[]> => {
    const term = (segment ?? '').toLowerCase().trim();
    const results = this.searchableCapacitadorOptions()
      .filter(opt => opt.nombre.toLowerCase().includes(term))
      .slice(0, limit);
    return of(results);
  };

  ngOnInit(): void {
    const role = this.authService.getUserRole() || '';
    this.isAdmin.set(role.toUpperCase() === 'ADMINISTRADOR');

    this.initForms();

    this.loadCatalogos();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {

      this.isEditMode.set(true);
      this.loadCapacitacion(Number(idParam));
    } 
  }

  private initForms(): void {
    this.capacitacionForm = this.fb.group({
      tema: ['', Validators.required],
      tipo: ['CHARLA_5_MINUTOS', Validators.required],
      fechaProgramada: ['', Validators.required],
      duracionHoras: [1.0, [Validators.required, Validators.min(0.25)]],
      capacitadorId: [''],
      capacitadorIds: [[] as number[]],
      sedeIdFilter: [null as number | null]
    });
  }

  private loadCatalogos(): void {
    this.trabajadorService.getSedes().subscribe(res => {

      this.sedes.set(res);
    });
    
    this.trabajadorService.getTrabajadores(0, 100, 'ACTIVO').subscribe(res => {

      if (res && res.content) {
        this.trabajadores.set(res.content);
        // If we have pending selected trabajador ids from the server, apply them now
        const pending = this.pendingSelectedTrabajadorIds();
        if (pending && pending.length > 0) {
          // Ensure ids are numbers and exist in the trabajadores catalog
          const valid = pending.filter(id => res.content.some((t: any) => Number(t.id) === Number(id)));
          if (valid.length > 0) {
            this.selectedTrabajadorIds.set(valid);
          }
          this.pendingSelectedTrabajadorIds.set([]);
        }
      }
    });
    
    this.capacitacionService.getCapacitadores().subscribe(res => {
  
      this.capacitadores.set(res);

      // If we are editing and the backend already provided the capacitadorId,
      // try to initialize the searchable select initial option so the table renders.
      const editId = this.editCapacitadorId();
      if (editId != null) {
        // editId may be a single id or array
        const ids = Array.isArray(editId) ? editId : [editId];
        const found = this.searchableCapacitadorOptions().filter(opt => ids.includes(opt.id));
        if (found.length > 0) {
          this.selectedCapacitadorInitial.set(found);
          this.selectedCapacitadores.set(found);
          this.capacitacionForm.patchValue({ capacitadorIds: found.map(f => f.id) });
        }
      } else {
        // For create flow, default to first capacitador if available
        const first = this.searchableCapacitadorOptions()[0];
        if (first) {
          this.selectedCapacitadorInitial.set([first]);
          this.selectedCapacitadores.set([first]);
          // also set form control default to first id
          this.capacitacionForm.patchValue({ capacitadorId: first.id, capacitadorIds: [first.id] });
        }
      }
    });
  }

  private loadCapacitacion(id: number): void {
    this.capacitacionService.getCapacitacionById(id).subscribe({
      next: (cap: any) => {

        
        // Patch the form with server values first so form control (and any value accessors)
        // receive the ids before we set selectedTrabajadorIds for rendering
        this.capacitacionForm.patchValue({
          tema: cap.tema,
          tipo: cap.tipo,
          fechaProgramada: cap.fechaProgramada,
          duracionHoras: cap.duracionHoras,
          capacitadorId: cap.capacitadorId ?? '',
          sedeIdFilter: cap.sedeIdFilter ?? null
        });

        // Load trabajadores - try multiple property names
        let trabajadorIds: number[] = [];

        
        if (Array.isArray(cap.trabajadores) && cap.trabajadores.length > 0) {
          trabajadorIds = cap.trabajadores.map((t: any) => Number(t.id || t.trabajador_id)).filter((n: number) => !isNaN(n));

        } else if (Array.isArray(cap.trabajadoresIds) && cap.trabajadoresIds.length > 0) {
          trabajadorIds = cap.trabajadoresIds.map((v: any) => Number(v)).filter((n: number) => !isNaN(n));
        } else if (Array.isArray(cap.trabajadoresSeleccionados) && cap.trabajadoresSeleccionados.length > 0) {
          trabajadorIds = cap.trabajadoresSeleccionados.map((t: any) => Number(t.id || t.trabajador_id)).filter((n: number) => !isNaN(n));
        } else if (Array.isArray(cap.equipoTrabajo) && cap.equipoTrabajo.length > 0) {
          trabajadorIds = cap.equipoTrabajo.map((t: any) => Number(t.id || t.trabajador_id)).filter((n: number) => !isNaN(n));
        } else if (Array.isArray(cap.capacitacionTrabajadores) && cap.capacitacionTrabajadores.length > 0) {
          trabajadorIds = cap.capacitacionTrabajadores.map((ct: any) => Number(ct.trabajador_id || ct.trabajadorId || ct.id)).filter((n: number) => !isNaN(n));
        }
        
        if (trabajadorIds.length > 0) {
          // If trabajadores catalog already loaded, apply selection immediately; otherwise store pending ids
          if (this.trabajadores().length > 0) {
            this.selectedTrabajadorIds.set(trabajadorIds);
          } else {
            this.pendingSelectedTrabajadorIds.set(trabajadorIds);
          }
        }
        

        // store edit capacitador id so when capacitadores list loads we can set initial option
        this.editCapacitadorId.set(cap.capacitadorId ?? null);

        // If backend provides capacitadores array, prefer it. Do NOT try to read searchableCapacitadorOptions() here
        // because that may trigger writes before the capacitadores catalog finishes loading and lead to change-detection cycles.
        this.editCapacitadorId.set(cap.capacitadorId ?? null);
        if (Array.isArray(cap.capacitadores) && cap.capacitadores.length > 0) {
          const caps = cap.capacitadores.map((c: any) => ({
            id: c.id,
            nombre: `${c.nombres || c.nombreCompleto || ''} ${c.apellidos ?? ''} · ${c.empresa ?? ''}`.trim()
          }));
          this.selectedCapacitadorInitial.set(caps);
          this.selectedCapacitadores.set(caps);
          this.capacitacionForm.patchValue({ capacitadorIds: caps.map((c: any) => c.id) });
        }

        // If backend provides responsables, initialize selector
        if (Array.isArray(cap.responsables) && cap.responsables.length > 0) {
          const responsables = cap.responsables.map((r: any) => ({
            id: r.id,
            nombre: `${r.nombreCompleto || (r.nombres + ' ' + (r.apellidos ?? ''))} · ${r.cargoNombre ?? 'Sin cargo'} · ${r.sedeNombre ?? 'Sin sede'}`
          }));
          this.responsablesSeleccionados.set(responsables);
        }

        // Load video links - try multiple property names

        
        const videoLinksCandidates = [
          cap.linksVideo,
          cap.videoLinks,
          cap.videos,
          cap.videoLinks?.map((v: any) => v.link_video || v.linkVideo || v.url || v),
          cap.capacitacionVideos?.map((v: any) => v.link_video || v.linkVideo || v.url)
        ];
        
        for (const candidate of videoLinksCandidates) {
          if (Array.isArray(candidate) && candidate.length > 0) {
            this.videoLinks.set(candidate);

            break;
          }
        }

        // Load form links - try multiple property names

        const formLinksCandidates = [
          cap.linksEvaluacion,
          cap.formLinks,
          cap.evaluaciones,
          cap.formularios,
          cap.linksEvaluacion?.map((f: any) => f.link_formulario || f.linkFormulario || f.url || f),
          cap.capacitacionEvaluaciones?.map((e: any) => e.link_formulario || e.linkFormulario || e.url)
        ];
        
        for (const candidate of formLinksCandidates) {
          if (Array.isArray(candidate) && candidate.length > 0) {
            this.formLinks.set(candidate);

            break;
          }
        }
      },
      error: (err) => {
        this.errorMessage.set('No se pudo cargar la capacitación.');
      }
    });

    // The single GET /capacitaciones/{id} already populates trabajadores, linksVideo and linksEvaluacion
    // so do not issue additional requests here to avoid duplicate writes and change-detection issues.
    // this.loadTrabajadoresAsignados(id);
    // this.loadVideosYEvaluaciones(id);
  }

  // Método para cargar trabajadores asignados a una capacitación
  private loadTrabajadoresAsignados(capacitacionId: number): void {
    this.capacitacionService.getTrabajadoresAsignados(capacitacionId).subscribe({
      next: (trabajadores: any[]) => {
        
        if (Array.isArray(trabajadores) && trabajadores.length > 0) {
          const ids = trabajadores.map((t: any) => Number(t.id || t.trabajador_id)).filter((n: number) => !isNaN(n));
          
          this.selectedTrabajadorIds.set(ids);
        }
      },
      error: (err) => {
        this.errorMessage.set('No se pudieron cargar los trabajadores asignados.');
        // No es error crítico, solo log
      }
    });
  }

  // Método para cargar videos y evaluaciones
  private loadVideosYEvaluaciones(capacitacionId: number): void {
    // Cargar videos
    this.capacitacionService.getVideosCapacitacion(capacitacionId).subscribe({
      next: (videos: any[]) => {

        if (Array.isArray(videos) && videos.length > 0) {
          // Extraer URLs si vienen como objetos, o usar directamente si son strings
          const links = videos.map((v: any) => v.link_video || v.linkVideo || v.url || v).filter((v: any) => v);

          this.videoLinks.set(links);
        }
      },
      error: (err) => {
        this.errorMessage.set('No se pudieron cargar los videos.');
        // No es error crítico, solo log
      }
    });

    // Cargar evaluaciones
    this.capacitacionService.getEvaluacionesCapacitacion(capacitacionId).subscribe({
      next: (evaluaciones: any[]) => {

        if (Array.isArray(evaluaciones) && evaluaciones.length > 0) {
          // Extraer URLs si vienen como objetos, o usar directamente si son strings
          const links = evaluaciones.map((e: any) => e.link_formulario || e.linkFormulario || e.url || e).filter((e: any) => e);

          this.formLinks.set(links);
        }
      },
      error: (err) => {
        this.errorMessage.set('No se pudieron cargar las evaluaciones.');
        // No es error crítico, solo log
      }
    });
  }

  protected submit(): void {
    if (!this.isAdmin() || this.capacitacionForm.invalid) {
      this.capacitacionForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const formVal = this.capacitacionForm.value;
    // Determine capacitador ids to send: prefer explicit capacitadorIds, then selectedCapacitadores, then single capacitadorId
    const capacitadorIdsFromForm: number[] = Array.isArray(formVal.capacitadorIds) ? formVal.capacitadorIds.map((v: any) => Number(v)).filter((n: number) => !isNaN(n)) : [];
    const capacitadorIdsFromSelected: number[] = this.selectedCapacitadores().length > 0 ? this.selectedCapacitadores().map(c => c.id) : [];
    const capacitadorIdsToSend = capacitadorIdsFromForm.length > 0 ? capacitadorIdsFromForm : (capacitadorIdsFromSelected.length > 0 ? capacitadorIdsFromSelected : (formVal.capacitadorId ? [Number(formVal.capacitadorId)] : []));

    const req: CrearCapacitacionRequest = {
      tema: formVal.tema,
      tipo: formVal.tipo,
      fechaProgramada: formVal.fechaProgramada,
      duracionHoras: Number(formVal.duracionHoras),
      // keep backward-compatible single field if needed by backend; prefer sending array
      capacitadorId: capacitadorIdsToSend.length > 0 ? capacitadorIdsToSend[0] : (formVal.capacitadorId ? Number(formVal.capacitadorId) : 0),
      capacitadorIds: capacitadorIdsToSend.length > 0 ? capacitadorIdsToSend : undefined,
      sedeIdFilter: formVal.sedeIdFilter ?? undefined,
      trabajadoresIds: this.selectedTrabajadorIds().length > 0 ? this.selectedTrabajadorIds() : undefined,
      responsablesIds: this.responsablesSeleccionados().length > 0 ? this.responsablesSeleccionados().map(r => r.id) : undefined,
      linksVideo: this.videoLinks().length > 0 ? this.videoLinks() : undefined,
      linksEvaluacion: this.formLinks().length > 0 ? this.formLinks() : undefined
    };

    // validation: ensure at least one capacitador
    if (!Array.isArray(req.capacitadorIds) || req.capacitadorIds.length === 0) {
      this.saving.set(false);
      this.notificationService.showError('Se requiere al menos un capacitador para programar la capacitación.');
      this.capacitacionForm.markAllAsTouched();
      return;
    }

    const idParam = this.route.snapshot.paramMap.get('id');
    const action = idParam
      ? this.capacitacionService.updateCapacitacion(Number(idParam), req)
      : this.capacitacionService.programarCapacitacion(req);

    action.subscribe({
      next: () => {
        this.saving.set(false);
        const message = this.isEditMode() ? 'Capacitación actualizada correctamente.' : 'Capacitación programada correctamente.';
        this.notificationService.showSuccess(message);
        setTimeout(() => {
          this.router.navigate(['/capacitaciones']);
        }, 1500);
      },
      error: (err) => {
        this.saving.set(false);
        const errorMsg = err.error?.message || 'No se pudo guardar la capacitación.';
        this.notificationService.showError(errorMsg);
      }
    });
  }

  protected goBack(): void {
    this.router.navigate(['/capacitaciones']);
  }

  protected onTrabajadoresSelectionChange(selected: number[]): void {
    this.selectedTrabajadorIds.set(selected);
  }

  // Called when the capacitador searchable-select emits a selectionChange
  protected onCapacitadorChange(selection: SearchableSelectOption[]): void {
    if (!selection || selection.length === 0) {
      this.capacitacionForm.patchValue({ capacitadorId: '', capacitadorIds: [] });
      this.selectedCapacitadores.set([]);
      this.selectedCapacitadorInitial.set([]);
      return;
    }

    // selection is an array of selected capacitadores
    this.selectedCapacitadores.set(selection);
    // Do not set selectedCapacitadorInitial here — initialSelectedOptions is an input used only for initialisation
    // Setting it here would cause the child to emit selectionChange again and may create an infinite loop.
    const ids = selection.map(s => s.id);
    this.capacitacionForm.patchValue({ capacitadorIds: ids, capacitadorId: ids[0] ?? '' });
  }

  protected onCapacitadorRemove(id: number): void {
    const next = this.selectedCapacitadores().filter(c => c.id !== id);
    this.selectedCapacitadores.set(next);
    // Do not update selectedCapacitadorInitial here to avoid creating a write->emit loop with the child component
    this.capacitacionForm.patchValue({ capacitadorIds: next.map(c => c.id), capacitadorId: next[0]?.id ?? '' });
  }
}

