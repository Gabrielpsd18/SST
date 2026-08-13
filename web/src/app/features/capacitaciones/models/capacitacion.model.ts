export interface Capacitador {
  id: number;
  nombres: string;
  apellidos?: string;
  empresa: string;
  telefono: string;
  correo?: string;
  especialidad?: string;
  estado: boolean;
}

export interface CapacitadorRequest {
  nombres: string;
  apellidos?: string;
  empresa: string;
  telefono: string;
  correo?: string;
  especialidad?: string;
}

export interface Capacitacion {
  id: number;
  tema: string;
  tipo: 'CHARLA_5_MINUTOS' | 'INDUCCION' | 'CAPACITACION';
  fechaProgramada: string;
  duracionHoras: number;
  capacitadorId: number;
  capacitadorNombre: string;
  capacitadorEmpresa: string;
  creadoPor: string;
  estado: string;
  totalTrabajadores: number;
  createdAt: string;
}

export interface CrearCapacitacionRequest {
  tema: string;
  tipo: string;
  fechaProgramada: string;
  duracionHoras: number;
  capacitadorId: number;
  sedeIdFilter?: number;
  trabajadoresIds?: number[];
  responsablesIds?: number[];
  capacitadorIds?: number[];
  videoLinks?: string[];
  formLinks?: string[];
}
