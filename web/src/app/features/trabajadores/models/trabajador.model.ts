export interface Trabajador {
  id: number;
  tipoDocumento: string;
  numeroDocumento: string;
  nombreCompleto: string;
  telefono?: string;
  correoNotificaciones?: string;
  tipoContrato: string;
  estado: string;
  sedeId: number;
  sedeNombre: string;
  cargoId: number;
  cargoNombre: string;
  usuarioId?: number;
  createdAt?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface CrearTrabajadorRequest {
  numeroDocumento: string;
  nombreCompleto: string;
  telefono?: string;
  correoNotificaciones?: string;
  sedeId: number;
  cargoId: number;
}

export interface ActualizarTrabajadorRequest {
  nombreCompleto: string;
  telefono?: string;
  correoNotificaciones?: string;
  sedeId: number;
  cargoId: number;
  estado: string;
}

export interface MaestraItem {
  id: number;
  nombre: string;
}

export interface CapacitacionItem {
  id: number;
  titulo: string;
  tipo: '5 MINUTOS' | 'INDUCCION' | 'CAPACITACION';
  fecha: string;
  estado: 'APROBADO' | 'PENDIENTE' | 'EN_CURSO';
  horas: number;
}

export interface DocumentoItem {
  id: number;
  nombre: string;
  categoria: 'EMO' | 'CONTRATO' | 'CERTIFICADO' | 'IDENTIFICACION';
  fechaEmision: string;
  estado: 'VIGENTE' | 'POR_VENCER' | 'VENCIDO';
}
