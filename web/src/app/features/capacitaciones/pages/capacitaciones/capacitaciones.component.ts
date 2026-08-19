import { Component, inject, OnInit, signal, computed} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  LucideGraduationCap,
  LucidePlus,
  LucideSearch,
  LucideUserCheck,
  LucideBuilding2,
  LucideCalendar,
  LucideClock,
  LucideBriefcase,
  LucideUsers,
  LucideShield,
  LucideLoader2,
  LucideChevronLeft,
  LucideChevronRight,
  LucideX,
  LucidePhone,
  LucideMail,
  LucideAward,
  LucideCheckCircle,
  LucideAlertCircle
} from '@lucide/angular';

import { CapacitacionService } from '../../services/capacitacion.service';
import { TrabajadorService } from '../../../trabajadores/services/trabajador.service';
import { AuthService } from '../../../auth/services/auth.service';
import { SearchableSelectComponent } from '../../../../shared/components/ui/searchable-select/searchable-select.component';
import {
  Capacitacion,
  Capacitador,
  CrearCapacitacionRequest,
  CapacitadorRequest
} from '../../models/capacitacion.model';
import { MaestraItem, Trabajador } from '../../../trabajadores/models/trabajador.model';
import { Router } from '@angular/router';
@Component({
  selector: 'app-capacitaciones',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SearchableSelectComponent],
  templateUrl: './capacitaciones.component.html',
  styleUrl: './capacitaciones.component.scss'
})
export class CapacitacionesComponent implements OnInit {
  private readonly capacitacionService = inject(CapacitacionService);
  private readonly trabajadorService = inject(TrabajadorService);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  // Iconos
  protected readonly GraduationCap = LucideGraduationCap;
  protected readonly Plus = LucidePlus;
  protected readonly Search = LucideSearch;
  protected readonly UserCheck = LucideUserCheck;
  protected readonly Building2 = LucideBuilding2;
  protected readonly Calendar = LucideCalendar;
  protected readonly Clock = LucideClock;
  protected readonly Briefcase = LucideBriefcase;
  protected readonly Users = LucideUsers;
  protected readonly Shield = LucideShield;
  protected readonly Loader2 = LucideLoader2;
  protected readonly ChevronLeft = LucideChevronLeft;
  protected readonly ChevronRight = LucideChevronRight;
  protected readonly X = LucideX;
  protected readonly Phone = LucidePhone;
  protected readonly Mail = LucideMail;
  protected readonly Award = LucideAward;
  protected readonly CheckCircle = LucideCheckCircle;
  protected readonly AlertCircle = LucideAlertCircle;

  // Control de Pestaña Principal (Sesiones vs Capacitadores)
  protected activeMainTab = signal<'sesiones' | 'capacitadores'>('sesiones');

  // Estado del usuario y rol
  protected isAdmin = signal<boolean>(false);

  // --- Estado de Capacitaciones ---
  protected capacitaciones = signal<Capacitacion[]>([]);
  protected estadoFiltroCapacitaciones = signal<'TODOS' | 'PROGRAMADO' | 'REALIZADO' | 'CANCELADO'>('TODOS');
  protected capacitacionesFiltradas = computed(() => {
    const filtro = this.estadoFiltroCapacitaciones();
    if (!filtro || filtro === 'TODOS') return this.capacitaciones();
    return this.capacitaciones().filter(c => (c.estado ?? '').toUpperCase() === filtro);
  });
  protected totalElements = signal<number>(0);
  protected totalPages = signal<number>(0);
  protected currentPage = signal<number>(0);
  protected pageSize = signal<number>(8);
  protected loadingCapacitaciones = signal<boolean>(true);
  private readonly router = inject(Router);

  // --- Estado de Capacitadores ---
  protected capacitadores = signal<Capacitador[]>([]);
  protected loadingCapacitadores = signal<boolean>(true);

  // --- Modales ---
  protected showCapacitacionModal = signal<boolean>(false);
  protected showCapacitadorModal = signal<boolean>(false);

  protected capacitacionForm!: FormGroup;
  protected capacitadorForm!: FormGroup;

  protected savingCapacitacion = signal<boolean>(false);
  protected savingCapacitador = signal<boolean>(false);

  protected capSuccessMsg = signal<string>('');
  protected capErrorMsg = signal<string>('');
  protected trainerSuccessMsg = signal<string>('');
  protected trainerErrorMsg = signal<string>('');

  // Listas auxiliares para formulario de capacitación
  protected sedes = signal<MaestraItem[]>([]);
  protected trabajadores = signal<Trabajador[]>([]);

  protected selectedSedeFilter = signal<number | null>(null);
  protected selectedTrabajadorIds = signal<number[]>([]);

  protected totalAsignadosCount = computed(() => {
    if (this.selectedTrabajadorIds().length > 0) {
      return this.selectedTrabajadorIds().length;
    }
    const sId = this.selectedSedeFilter();
    let count = 0;
    for (const t of this.trabajadores()) {
      const matchSede = !sId || t.sedeId === sId;
      if (matchSede) count++;
    }
    return count > 0 ? count : this.trabajadores().length;
  });

  ngOnInit(): void {
    const role = this.authService.getUserRole() || '';
    this.isAdmin.set(role.toUpperCase() === 'ADMINISTRADOR');

    this.initForms();
    this.loadCapacitaciones();
    this.loadCapacitadores();
    this.loadCatalogos();
  }

  goToCapacitacionCrear(): void {
     if (!this.isAdmin()) {
      return;
    }
    this.router.navigate(['/capacitaciones/crear']);
  }

  goToEditCapacitacion(id: number): void {
    if (!this.isAdmin()) return;
    this.router.navigate([`/capacitaciones/${id}/editar`]);
  }
  private initForms(): void {
    this.capacitacionForm = this.fb.group({
      tema: ['', Validators.required],
      tipo: ['CHARLA_5_MINUTOS', Validators.required],
      fechaProgramada: ['', Validators.required],
      duracionHoras: [1.0, [Validators.required, Validators.min(0.25)]],
      capacitadorId: ['', Validators.required],
      sedeIdFilter: [null as number | null]
    });

    this.capacitacionForm.get('sedeIdFilter')?.valueChanges.subscribe((value: number | null) => {
      this.selectedSedeFilter.set(value);
    });

    this.capacitadorForm = this.fb.group({
      nombres: ['', Validators.required],
      apellidos: [''],
      empresa: ['', Validators.required],
      telefono: ['', [Validators.required, Validators.pattern('^[0-9]{9}$')]],
      correo: ['', [Validators.email]],
      especialidad: ['']
    });
  }

  setMainTab(tab: 'sesiones' | 'capacitadores'): void {
    this.activeMainTab.set(tab);
  }

  setEstadoFiltroCapacitaciones(estado: 'TODOS' | 'PROGRAMADO' | 'REALIZADO' | 'CANCELADO'): void {
    this.estadoFiltroCapacitaciones.set(estado);
  }

  loadCapacitaciones(page: number = 0): void {
    this.loadingCapacitaciones.set(true);
    this.currentPage.set(page);

    this.capacitacionService.getCapacitaciones(page, this.pageSize()).subscribe({
      next: (response) => {
        if (response && response.content) {
          this.capacitaciones.set(response.content);
          this.totalElements.set(response.totalElements);
          this.totalPages.set(response.totalPages);
        }
        this.loadingCapacitaciones.set(false);
      },
      error: () => this.loadingCapacitaciones.set(false)
    });
  }

  loadCapacitadores(): void {
    this.loadingCapacitadores.set(true);
    this.capacitacionService.getCapacitadores().subscribe({
      next: (res) => {
        this.capacitadores.set(res);
        this.loadingCapacitadores.set(false);
      },
      error: () => this.loadingCapacitadores.set(false)
    });
  }

  private loadCatalogos(): void {
    this.trabajadorService.getSedes().subscribe(res => this.sedes.set(res));
    this.trabajadorService.getTrabajadores(0, 100, 'ACTIVO').subscribe(res => {
      if (res && res.content) this.trabajadores.set(res.content);
    });
  }

  // Paginación
  prevPage(): void {
    if (this.currentPage() > 0) this.loadCapacitaciones(this.currentPage() - 1);
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) this.loadCapacitaciones(this.currentPage() + 1);
  }

  // Modal Programar Capacitación
  openCapacitacionModal(): void {
    if (!this.isAdmin()) return;
    this.capSuccessMsg.set('');
    this.capErrorMsg.set('');
    this.selectedSedeFilter.set(null);
    this.selectedTrabajadorIds.set([]);

    // Fecha actual formateada para input datetime-local
    const now = new Date();
    now.setHours(now.getHours() + 24); // Mañana
    const formattedDate = now.toISOString().slice(0, 16);

    this.capacitacionForm.reset({
      tema: '',
      tipo: 'CHARLA_5_MINUTOS',
      fechaProgramada: formattedDate,
      duracionHoras: 1.0,
      capacitadorId: this.capacitadores()[0]?.id || '',
      sedeIdFilter: null
    });

    this.showCapacitacionModal.set(true);
  }

  closeCapacitacionModal(): void {
    this.showCapacitacionModal.set(false);
  }

  submitCapacitacion(): void {
    if (this.capacitacionForm.invalid || !this.isAdmin()) return;

    this.savingCapacitacion.set(true);
    this.capSuccessMsg.set('');
    this.capErrorMsg.set('');

    const formVal = this.capacitacionForm.value;

    const req: CrearCapacitacionRequest = {
      tema: formVal.tema,
      tipo: formVal.tipo,
      fechaProgramada: formVal.fechaProgramada,
      duracionHoras: Number(formVal.duracionHoras),
      capacitadorId: Number(formVal.capacitadorId),
      sedeIdFilter: formVal.sedeIdFilter ?? undefined,
      trabajadoresIds: this.selectedTrabajadorIds().length > 0 ? this.selectedTrabajadorIds() : undefined
    };

    this.capacitacionService.programarCapacitacion(req).subscribe({
      next: () => {
        this.savingCapacitacion.set(false);
        this.capSuccessMsg.set('Capacitación programada correctamente.');
        setTimeout(() => {
          this.closeCapacitacionModal();
          this.loadCapacitaciones(0);
        }, 1200);
      },
      error: (err) => {
        this.savingCapacitacion.set(false);
        this.capErrorMsg.set(err.error?.message || 'Error al programar la capacitación.');
      }
    });
  }

  // Modal Crear Capacitador
  openCapacitadorModal(): void {
    if (!this.isAdmin()) return;
    this.trainerSuccessMsg.set('');
    this.trainerErrorMsg.set('');
    this.capacitadorForm.reset({
      nombres: '',
      apellidos: '',
      empresa: '',
      telefono: '',
      correo: '',
      especialidad: ''
    });
    this.showCapacitadorModal.set(true);
  }

  closeCapacitadorModal(): void {
    this.showCapacitadorModal.set(false);
  }

  submitCapacitador(): void {
    if (this.capacitadorForm.invalid || !this.isAdmin()) return;

    this.savingCapacitador.set(true);
    this.trainerSuccessMsg.set('');
    this.trainerErrorMsg.set('');

    const req: CapacitadorRequest = this.capacitadorForm.value;

    this.capacitacionService.createCapacitador(req).subscribe({
      next: () => {
        this.savingCapacitador.set(false);
        this.trainerSuccessMsg.set('Capacitador registrado exitosamente.');
        setTimeout(() => {
          this.closeCapacitadorModal();
          this.loadCapacitadores();
        }, 1200);
      },
      error: (err) => {
        this.savingCapacitador.set(false);
        this.trainerErrorMsg.set(err.error?.message || 'Error al registrar el capacitador.');
      }
    });
  }
}
