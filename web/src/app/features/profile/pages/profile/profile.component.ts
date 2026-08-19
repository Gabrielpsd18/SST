import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideShield, LucideBuilding2, LucideMail, LucideAward, LucideSave, LucideLoader2 } from '@lucide/angular';

import { UserService } from '../../services/user.service';
import { UserProfile, UpdateProfileRequest } from '../../models/user-profile.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);

  protected readonly Shield = LucideShield;
  protected readonly Building2 = LucideBuilding2;
  protected readonly Mail = LucideMail;
  protected readonly Award = LucideAward;
  protected readonly Save = LucideSave;
  protected readonly Loader2 = LucideLoader2;

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
      nombreCompleto: [{ value: '', disabled: true }],
      correoCorporativo: [{ value: '', disabled: true }],
      sede: [{ value: '', disabled: true }],
      cargo: [{ value: '', disabled: true }],
      correoNotificaciones: ['', [Validators.required, Validators.email]],
      telefono: ['', [Validators.required, Validators.pattern('^[0-9]{9}$')]],
      email: ['', [Validators.required, Validators.email]],
      password: ['']
    });
  }

  private loadUserProfile(): void {
    this.loadingData.set(true);
    this.errorMessage.set('');

    this.userService.getProfile().subscribe({
      next: (response) => {
        if (response && response.data) {
          const data = response.data;
          this.profileData.set(data);

          this.profileForm.patchValue({
            dni: data.dni,
            nombreCompleto: data.nombreCompleto,
            correoCorporativo: data.correoCorporativo,
            sede: data.sede,
            cargo: data.cargo,
            correoNotificaciones: data.correoNotificaciones,
            telefono: data.telefono,
            email: data.email
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

  getInitials(nombreCompleto: string): string {
    const parts = nombreCompleto.trim().split(/\s+/);
    if (parts.length === 0) return '?';
    if (parts.length === 1) return parts[0][0]?.toUpperCase() ?? '?';
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  saveProfile(): void {
    if (this.profileForm.invalid) return;

    this.saving.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');

    const request: UpdateProfileRequest = {
      correoNotificaciones: this.profileForm.value.correoNotificaciones,
      telefono: this.profileForm.value.telefono,
      email: this.profileForm.value.email,
      password: this.profileForm.value.password || undefined
    };

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
