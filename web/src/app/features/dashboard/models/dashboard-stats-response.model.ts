export interface DashboardStatsResponse {
  totalTrabajadores: number;
  totalCapacitaciones: number;
  totalInspecciones: number;

  trabajadoresPorSede: Record<string, number>;
  trabajadoresPorCargo: Record<string, number>;

  capacitacionesPorEstado: Record<string, number>;
  capacitacionesPorTipo: Record<string, number>;

  inspeccionesPorEstado: Record<string, number>;
  inspeccionesPorTipo: Record<string, number>;

  actividadReciente: RecentActivityDTO[];
  proximosEventos: UpcomingEventDTO[];
}

export interface RecentActivityDTO {
  id: number;
  tipo: 'CAPACITACION' | 'INSPECCION' | string;
  titulo: string;
  fecha: string;
  detalle: string;
  estado: string;
}

export interface UpcomingEventDTO {
  id: number;
  tipo: 'CAPACITACION' | 'INSPECCION' | string;
  titulo: string;
  fecha: string;
  detalle: string;
  estado: string;
}