export interface UserProfile {
  telefono: string;
  correoNotificaciones: string;

  id: number;
  dni: string;
  nombreCompleto: string;
  correoCorporativo: string;

  sede: string;
  cargo: string;
  fechaIngreso?: string;

  capacitacionesCompletadas?: number;
  capacitacionesPendientes?: number;
}

export interface UpdateProfileRequest {
  telefono: string;
  correoNotificaciones: string;
}
