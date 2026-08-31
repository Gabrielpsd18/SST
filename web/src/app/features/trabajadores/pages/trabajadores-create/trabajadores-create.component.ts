import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import {LucidePlus } from '@lucide/angular';

import { TrabajadorService } from '../../services/trabajador.service';
import { AuthService } from '../../../auth/services/auth.service';
import { SearchableSelectComponent } from '../../../../shared/components/ui/searchable-select/searchable-select.component';
import { MaestraItem, CrearTrabajadorRequest } from '../../models/trabajador.model';

@Component({
  selector: 'app-trabajadores-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, SearchableSelectComponent, LucidePlus],
  templateUrl: './trabajadores-create.component.html',
  styleUrl: './trabajadores-create.component.scss'
})
export class TrabajadoresCreateComponent implements OnInit {
  private readonly trabajadorService = inject(TrabajadorService);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);

  protected readonly Plus = LucidePlus;

  protected isAdmin = signal<boolean>(false);
  
  protected createForm!: FormGroup;
  protected sedes = signal<MaestraItem[]>([]);
  protected cargos = signal<MaestraItem[]>([]);
  
  protected savingTrabajador = signal<boolean>(false);
  protected modalSuccessMsg = signal<string>('');
  protected modalErrorMsg = signal<string>('');

  ngOnInit(): void {
    const role = this.authService.getUserRole() || '';
    this.isAdmin.set(role.toUpperCase() === 'ADMINISTRADOR');

    // Si no es admin, opcionalmente podrías redirigirlo fuera de esta página
    if (!this.isAdmin()) {
      this.router.navigate(['/trabajadores']);
      return;
    }

    this.initCreateForm();
    this.loadCatalogos();
  }

  private initCreateForm(): void {
    this.createForm = this.fb.group({
      numeroDocumento: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      nombreCompleto: ['', [Validators.required, Validators.maxLength(200)]],
      telefono: ['', [Validators.pattern('(^$|^[0-9]{9}$)')]],
      correoNotificaciones: ['', [Validators.pattern('(^$|.+@.+\\..+)')]],
      sedeId: [null, Validators.required],
      cargoId: [null, Validators.required]
    });
  }

  private loadCatalogos(): void {
    this.trabajadorService.getSedes().subscribe({
      next: (res) => {
        this.sedes.set(res);
        // Autoseleccionar el primero si está disponible
        if (res.length > 0) this.createForm.patchValue({ sedeId: res[0].id });
      }
    });
    
    this.trabajadorService.getCargos().subscribe({
      next: (res) => {
        this.cargos.set(res);
        // Autoseleccionar el primero si está disponible
        if (res.length > 0) this.createForm.patchValue({ cargoId: res[0].id });
      }
    });
  }

  submitCreateTrabajador(): void {
    if (this.createForm.invalid || !this.isAdmin()) return;

    this.savingTrabajador.set(true);
    this.modalSuccessMsg.set('');
    this.modalErrorMsg.set('');

    const formVal = this.createForm.value;
    const req: CrearTrabajadorRequest = {
      numeroDocumento: formVal.numeroDocumento,
      nombreCompleto: formVal.nombreCompleto,
      telefono: formVal.telefono || undefined,
      correoNotificaciones: formVal.correoNotificaciones || undefined,
      sedeId: Number(formVal.sedeId),
      cargoId: Number(formVal.cargoId)
    };

    this.trabajadorService.createTrabajador(req).subscribe({
      next: () => {
        this.savingTrabajador.set(false);
        this.modalSuccessMsg.set('Trabajador registrado exitosamente');
      },
      error: (err) => {
        this.savingTrabajador.set(false);
        this.modalErrorMsg.set(err.error?.message || 'Error al registrar el trabajador.');
      }
    });
  }
}