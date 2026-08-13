import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ArrowLeft, CheckCircle, Loader2, LucideAngularModule, Save, Search, Users } from 'lucide-angular';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { AuthService } from '../../../auth/services/auth.service';
import { TrabajadorService } from '../../../trabajadores/services/trabajador.service';
import { SearchableSelectComponent, SearchableSelectOption } from '../../../../shared/components/ui/searchable-select/searchable-select.component';
import { CrearInspeccionRequest, Inspeccion } from '../../models/inspeccion.model';
import { InspeccionesService } from '../../services/inspecciones.service';

@Component({
  selector: 'app-inspecciones-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LucideAngularModule, SearchableSelectComponent],
  templateUrl: './inspecciones-form.component.html',
  styleUrl: './inspecciones-form.component.scss'
})
export class InspeccionesFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly inspeccionesService = inject(InspeccionesService);
  private readonly trabajadorService = inject(TrabajadorService);

  protected readonly ArrowLeft = ArrowLeft;
  protected readonly Users = Users;
  protected readonly Search = Search;
  protected readonly Save = Save;
  protected readonly CheckCircle = CheckCircle;
  protected readonly Loader2 = Loader2;

  protected readonly isAdmin = signal<boolean>(false);
  protected readonly saving = signal<boolean>(false);
  protected readonly errorMessage = signal<string>('');
  protected readonly isEditMode = signal<boolean>(false);
  protected readonly responsablesSeleccionados = signal<SearchableSelectOption[]>([]);
  protected form!: FormGroup;

  ngOnInit(): void {
    const role = this.authService.getUserRole() || '';
    this.isAdmin.set(role.toUpperCase() === 'ADMINISTRADOR');

    if (!this.isAdmin()) {
      this.router.navigate(['/inspecciones']);
      return;
    }

    this.form = this.fb.group({
      tema: ['', Validators.required],
      tipo: ['PLANEADA', Validators.required],
      fechaInspeccion: [this.getDefaultDate(), Validators.required],
      horaInspeccion: ['09:00', Validators.required],
      observaciones: [''],
      responsableIds: [[], Validators.required]
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode.set(true);
      this.loadInspeccion(Number(idParam));
    }
  }

  protected searchResponsables = (segment: string, limit: number): Observable<SearchableSelectOption[]> =>
    this.trabajadorService.searchTrabajadores(segment, limit).pipe(
      map((trabajadores) =>
        trabajadores.map((trabajador) => ({
          id: trabajador.id,
          nombre: `${trabajador.nombreCompleto} · ${trabajador.cargoNombre ?? 'Sin cargo'} · ${trabajador.sedeNombre ?? 'Sin sede'}`
        }))
      )
    );

  protected onResponsablesChange(responsables: SearchableSelectOption[]): void {
    this.responsablesSeleccionados.set(responsables);
  }

  private loadInspeccion(id: number): void {
    this.inspeccionesService.getInspeccionById(id).subscribe({
      next: (inspeccion: Inspeccion) => {
        const responsables = inspeccion.responsables.map((responsable) => ({
          id: responsable.id,
          nombre: `${responsable.nombreCompleto} · ${responsable.cargoNombre ?? 'Sin cargo'} · ${responsable.sedeNombre ?? 'Sin sede'}`
        }));

          this.form.patchValue({
            tema: inspeccion.tema,
            tipo: inspeccion.tipo,
            fechaInspeccion: inspeccion.fechaInspeccion,
            horaInspeccion: inspeccion.horaInspeccion,
            observaciones: inspeccion.observaciones ?? '',
            responsableIds: inspeccion.responsables.map((responsable) => responsable.id)
          });

          this.responsablesSeleccionados.set(responsables);
        },
      error: () => {
        this.errorMessage.set('No se pudo cargar la inspección.');
      }
    });
  }

  protected submit(): void {
    if (!this.isAdmin() || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set('');

    const rawResponsables = this.form.value.responsableIds ?? [];
    const responsableIds: number[] = Array.isArray(rawResponsables)
      ? Array.from(new Set(rawResponsables.map((v: any) => Number(v)).filter((n: number) => !isNaN(n))))
      : [];

    const responsablesParaGuardar = this.responsablesSeleccionados().length > 0
      ? this.responsablesSeleccionados().map((responsable) => responsable.id)
      : responsableIds;

    const request: CrearInspeccionRequest = {
      tema: this.form.value.tema,
      tipo: this.form.value.tipo,
      fechaInspeccion: this.form.value.fechaInspeccion,
      horaInspeccion: this.form.value.horaInspeccion,
      observaciones: this.form.value.observaciones || '',
      responsableIds: responsablesParaGuardar
    };

    const action = this.isEditMode()
      ? this.inspeccionesService.updateInspeccion(Number(this.route.snapshot.paramMap.get('id')), request)
      : this.inspeccionesService.createInspeccion(request);

    action.subscribe({
      next: () => {
        this.saving.set(false);
        this.router.navigate(['/inspecciones']);
      },
      error: (error) => {
        this.saving.set(false);
        this.errorMessage.set(error?.error?.message || 'No se pudo guardar la inspección.');
      }
    });
  }

  protected goBack(): void {
    this.router.navigate(['/inspecciones']);
  }

  private getDefaultDate(): string {
    const now = new Date();
    const offset = now.getTimezoneOffset();
    const local = new Date(now.getTime() - offset * 60 * 1000);
    return local.toISOString().slice(0, 10);
  }
}
