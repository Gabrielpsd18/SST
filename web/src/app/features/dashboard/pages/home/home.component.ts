import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../auth/services/auth.service';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardStatsResponse } from '../../models/dashboard-stats-response.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class HomeComponent implements OnInit {

  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly dashboardService = inject(DashboardService);

  protected readonly dashboard = signal<DashboardStatsResponse | null>(null);
  protected readonly loading = signal<boolean>(true);
  protected readonly error = signal<string>('');

  ngOnInit(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {
    this.loading.set(true);
    this.error.set('');

    this.dashboardService.obtenerEstadisticas().subscribe({
      next: (data) => {
        this.dashboard.set(data);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error al cargar dashboard:', error);

        this.error.set(
          'No se pudo cargar la información del dashboard.'
        );

        this.loading.set(false);
      }
    });
  }

  protected formatDate(date: string): string {
    if (!date) return '';

    return new Date(date).toLocaleString('es-PE', {
      dateStyle: 'short',
      timeStyle: 'short'
    });
  }

  protected getActivityIcon(tipo: string): string {
    return tipo === 'CAPACITACION' ? '📚' : '🔍';
  }

  protected getEventIcon(tipo: string): string {
    return tipo === 'CAPACITACION' ? '📚' : '🔍';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
  protected verDetalle(tipo: string, id: number): void {
    if (tipo === 'CAPACITACION') {
      this.router.navigate(['/capacitaciones', id, 'editar']);
      return;
    }

    if (tipo === 'INSPECCION') {
      this.router.navigate(['/inspecciones', id, 'editar']);
    }
  }
}