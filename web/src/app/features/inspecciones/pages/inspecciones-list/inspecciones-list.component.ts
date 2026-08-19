import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import {
  LucideAlertCircle,
  LucideCalendar,
  LucideCheckCircle,
  LucideChevronLeft,
  LucideChevronRight,
  LucideClock,
  LucideLoader2,
  LucidePencil,
  LucidePlus,
  LucideShield
} from '@lucide/angular';

import { AuthService } from '../../../auth/services/auth.service';
import { PaginatedResponse } from '../../../trabajadores/models/trabajador.model';
import { Inspeccion } from '../../models/inspeccion.model';
import { InspeccionesService } from '../../services/inspecciones.service';

@Component({
  selector: 'app-inspecciones-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inspecciones-list.component.html',
  styleUrl: './inspecciones-list.component.scss'
})
export class InspeccionesListComponent implements OnInit {
  private readonly inspeccionesService = inject(InspeccionesService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly Calendar = LucideCalendar;
  protected readonly Clock = LucideClock;
  protected readonly Plus = LucidePlus;
  protected readonly Pencil = LucidePencil;
  protected readonly Shield = LucideShield;
  protected readonly Loader2 = LucideLoader2;
  protected readonly CheckCircle = LucideCheckCircle;
  protected readonly AlertCircle = LucideAlertCircle;
  protected readonly ChevronLeft = LucideChevronLeft;
  protected readonly ChevronRight = LucideChevronRight;

  protected readonly isAdmin = signal<boolean>(false);
  protected readonly inspecciones = signal<Inspeccion[]>([]);
  protected readonly estadoFiltro = signal<'TODOS'|'PENDIENTE'|'REALIZADA'|'RETRASADA'|'INCUMPLIDA'>('TODOS');
  protected readonly inspeccionesFiltradas = computed(() => {
    const filtro = this.estadoFiltro();
    if (!filtro || filtro === 'TODOS') return this.inspecciones();
    return this.inspecciones().filter(i => (i.estado ?? '').toUpperCase() === filtro);
  });
  protected readonly loading = signal<boolean>(false);
  protected readonly totalElements = signal<number>(0);
  protected readonly totalPages = signal<number>(0);
  protected readonly currentPage = signal<number>(0);
  protected readonly pageSize = signal<number>(8);

  protected readonly summaryCounts = computed(() => {
    const counts = {
      pendiente: 0,
      realizada: 0,
      retrasada: 0,
      incumplida: 0
    };

    this.inspecciones().forEach((inspeccion) => {
      switch (inspeccion.estado) {
        case 'PENDIENTE':
          counts.pendiente++;
          break;
        case 'REALIZADA':
          counts.realizada++;
          break;
        case 'RETRASADA':
          counts.retrasada++;
          break;
        case 'INCUMPLIDA':
          counts.incumplida++;
          break;
      }
    });

    return counts;
  });

  ngOnInit(): void {
    this.isAdmin.set(this.authService.getUserRole()?.toUpperCase() === 'ADMINISTRADOR');
    this.loadInspecciones(0);
  }

  setEstadoFiltro(estado: 'TODOS'|'PENDIENTE'|'REALIZADA'|'RETRASADA'|'INCUMPLIDA'): void {
    this.estadoFiltro.set(estado);
  }

  loadInspecciones(page: number = 0): void {
    this.loading.set(true);
    this.currentPage.set(page);

    this.inspeccionesService.getInspecciones(page, this.pageSize()).subscribe({
      next: (response: PaginatedResponse<Inspeccion>) => {
        this.inspecciones.set(response.content ?? []);
        this.totalElements.set(response.totalElements ?? 0);
        this.totalPages.set(response.totalPages ?? 0);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  prevPage(): void {
    if (this.currentPage() > 0) {
      this.loadInspecciones(this.currentPage() - 1);
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.loadInspecciones(this.currentPage() + 1);
    }
  }

  goToCreate(): void {
    if (!this.isAdmin()) {
      return;
    }
    this.router.navigate(['/inspecciones/nuevo']);
  }

  goToEdit(inspeccion: Inspeccion): void {
    if (!this.isAdmin()) {
      return;
    }
    this.router.navigate(['/inspecciones', inspeccion.id, 'editar']);
  }

  getStatusClass(estado: string): string {
    switch (estado) {
      case 'PENDIENTE':
        return 'badge badge--pending';
      case 'REALIZADA':
        return 'badge badge--done';
      case 'RETRASADA':
        return 'badge badge--late';
      case 'INCUMPLIDA':
        return 'badge badge--failed';
      default:
        return 'badge';
    }
  }
}
