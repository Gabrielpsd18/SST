export interface UserProfile {
  id: number;
  dni: string;
  nombreCompleto: string;
  correoCorporativo: string;
  correoNotificaciones: string;
  telefono: string;
  sede: string;
  cargo: string;
  fechaIngreso?: string;
  capacitacionesCompletadas?: number;
  capacitacionesPendientes?: number;
  token?: string;
  email?: string;
}

export interface UpdateProfileRequest {
  telefono: string;
  correoNotificaciones: string;
  email?: string;
  password?: string;
}