import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/login-request';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent implements OnInit {

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

  login() {

    this.loading = true;
    this.errorMessage = '';

    const request: LoginRequest = {
      email: this.username,
      password: this.password
    };

    this.authService.login(request).subscribe({

      next: (response) => {

        this.loading = false;

        if (!response.success) {
          this.errorMessage = response.message;
          return;
        }

        this.authService.saveToken(response.data.accessToken);

        this.router.navigate(['/home']);

      },

      error: (err) => {

        this.loading = false;

        if (err.status === 0) {
          this.errorMessage = "No se pudo conectar con el servidor.";
        } else {
          this.errorMessage = "Correo o contraseña incorrectos.";
        }

        this.cdr.detectChanges();

      }
    });
    
  }

}

