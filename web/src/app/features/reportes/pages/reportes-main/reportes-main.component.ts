import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  DashboardService
} from '../../../dashboard/services/dashboard.service';

import {
  DashboardStatsResponse
} from '../../../dashboard/models/dashboard-stats-response.model';

@Component({
  selector: 'app-reportes-main',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reportes-main.component.html',
  styleUrl: './reportes-main.component.scss'
})
export class ReportesMainComponent implements OnInit {

  private readonly dashboardService = inject(DashboardService);

  protected dashboard = signal<DashboardStatsResponse | null>(null);
  protected loading = signal<boolean>(true);
  protected error = signal<string>('');

  ngOnInit(): void {
    this.cargarEstadisticas();
  }

  private cargarEstadisticas(): void {
    this.loading.set(true);
    this.error.set('');

    this.dashboardService.obtenerEstadisticas().subscribe({
      next: (data) => {
        this.dashboard.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(
          'No se pudieron cargar las estadísticas de reportes.'
        );
        this.loading.set(false);
      }
    });
  }

  protected descargarTrabajadoresCSV(): void {
    const data = this.dashboard();

    if (!data) return;

    const rows = Object.entries(data.trabajadoresPorSede).map(
      ([sede, cantidad]) => ({
        sede,
        cantidad
      })
    );

    this.descargarCSV(
      'reporte-trabajadores-por-sede.csv',
      rows,
      ['Sede', 'Cantidad']
    );
  }

  protected descargarCapacitacionesCSV(): void {
    const data = this.dashboard();

    if (!data) return;

    const rows = Object.entries(data.capacitacionesPorEstado).map(
      ([estado, cantidad]) => ({
        estado,
        cantidad
      })
    );

    this.descargarCSV(
      'reporte-capacitaciones-por-estado.csv',
      rows,
      ['Estado', 'Cantidad']
    );
  }

  protected descargarInspeccionesCSV(): void {
    const data = this.dashboard();

    if (!data) return;

    const rows = Object.entries(data.inspeccionesPorEstado).map(
      ([estado, cantidad]) => ({
        estado,
        cantidad
      })
    );

    this.descargarCSV(
      'reporte-inspecciones-por-estado.csv',
      rows,
      ['Estado', 'Cantidad']
    );
  }

  protected descargarResumenCSV(): void {
    const data = this.dashboard();

    if (!data) return;

    const rows = [
      {
        indicador: 'Total de trabajadores',
        cantidad: data.totalTrabajadores
      },
      {
        indicador: 'Total de capacitaciones',
        cantidad: data.totalCapacitaciones
      },
      {
        indicador: 'Total de inspecciones',
        cantidad: data.totalInspecciones
      }
    ];

    this.descargarCSV(
      'reporte-resumen-sst.csv',
      rows,
      ['Indicador', 'Cantidad']
    );
  }

  private descargarCSV(
    filename: string,
    rows: Record<string, string | number>[],
    headers: string[]
  ): void {

    if (rows.length === 0) {
      return;
    }

    const csvRows = [
      headers.join(','),
      ...rows.map(row =>
        Object.values(row)
          .map(value => this.escapeCSV(value))
          .join(',')
      )
    ];

    const csvContent = '\uFEFF' + csvRows.join('\n');

    const blob = new Blob(
      [csvContent],
      {
        type: 'text/csv;charset=utf-8;'
      }
    );

    const url = URL.createObjectURL(blob);

    const link = document.createElement('a');

    link.href = url;
    link.download = filename;

    document.body.appendChild(link);
    link.click();

    document.body.removeChild(link);

    URL.revokeObjectURL(url);
  }

  private escapeCSV(value: string | number): string {
    const text = String(value);

    if (
      text.includes(',') ||
      text.includes('"') ||
      text.includes('\n')
    ) {
      return `"${text.replace(/"/g, '""')}"`;
    }

    return text;
  }
}