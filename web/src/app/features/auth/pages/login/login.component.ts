import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LucideAngularModule, Shield } from 'lucide-angular';

import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/login-request';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent implements OnInit {
  protected readonly Shield = Shield;

  username = '';
  password = '';
  loading = false;
  errorMessage = '';

  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const token = localStorage.getItem('access_token');
    if (token) {
      this.router.navigate(['/home']);
    }
  }

  login(): void {
    if (!this.username || !this.password) return;

    this.loading = true;
    this.errorMessage = '';

    const request: LoginRequest = {
      email: this.username,
      password: this.password
    };

    this.authService.login(request).subscribe({
      next: (response) => {
        this.loading = false;

        if (!response.success || !response.data) {
          this.errorMessage = response.message || 'Error al iniciar sesión.';
          this.cdr.detectChanges();
          return;
        }

        this.router.navigate(['/home']);
      },
      error: (err) => {
        this.loading = false;

        if (err.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor.';
        } else {
          this.errorMessage = 'Correo o contraseña incorrectos.';
        }

        this.cdr.detectChanges();
      }
    });
  }
}