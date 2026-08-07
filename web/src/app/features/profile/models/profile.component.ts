import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule, Shield, Building2, Mail, Award, Save, Loader2 } from 'lucide-angular';

import { UserService } from '../services/user.service'; // Ajusta la ruta a tu UserService
import { UserProfile, UpdateProfileRequest } from '../models/user-profile.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService); // Servicio correcto

  // Iconos
  protected readonly Shield = Shield;
  protected readonly Building2 = Building2;
  protected readonly Mail = Mail;
  protected readonly Award = Award;
  protected readonly Save = Save;
  protected readonly Loader2 = Loader2;

  protected profileForm!: FormGroup;
  protected loadingData = signal<boolean>(true);
  protected saving = signal<boolean>(false);
  protected successMessage = signal<string>('');
  protected errorMessage = signal<string>('');

  protected profileData = signal<UserProfile | null>(null);

  ngOnInit(): void {
    this.buildForm();
    this.loadUserProfile();
  }

  private buildForm(): void {
    this.profileForm = this.fb.group({
      dni: [{ value: '', disabled: true }],
      nombres: [{ value: '', disabled: true }],
      correoCorporativo: [{ value: '', disabled: true }],
      sede: [{ value: '', disabled: true }],
      area: [{ value: '', disabled: true }],
      cargo: [{ value: '', disabled: true }],
      // Campos editables por el trabajador
      correoNotificaciones: ['', [Validators.required, Validators.email]],
      telefono: ['', [Validators.required, Validators.pattern('^[0-9]{9}$')]]
    });
  }

  private loadUserProfile(): void {
    this.loadingData.set(true);

    // CORREGIDO: Llamada a userService.getProfile()
    this.userService.getProfile().subscribe({
      next: (response) => {
        if (response.data) {
          const data = response.data;
          this.profileData.set(data);

          this.profileForm.patchValue({
            dni: data.dni,
            nombres: `${data.nombres} ${data.apellidos}`,
            correoCorporativo: data.correoCorporativo,
            sede: data.sede,
            area: data.area,
            cargo: data.cargo,
            correoNotificaciones: data.correoNotificaciones,
            telefono: data.telefono
          });
        }
        this.loadingData.set(false);
      },
      error: () => {
        this.errorMessage.set('No se pudo conectar con el servidor para obtener los datos del perfil.');
        this.loadingData.set(false);
      }
    });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) return;

    this.saving.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');

    const request: UpdateProfileRequest = {
      correoNotificaciones: this.profileForm.value.correoNotificaciones,
      telefono: this.profileForm.value.telefono
    };

    // CORREGIDO: Llamada a userService.updateProfile()
    this.userService.updateProfile(request).subscribe({
      next: () => {
        this.saving.set(false);
        this.successMessage.set('Perfil actualizado correctamente.');
      },
      error: () => {
        this.saving.set(false);
        this.errorMessage.set('Error al actualizar los datos del perfil.');
      }
    });
  }
}