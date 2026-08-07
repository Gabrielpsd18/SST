export interface UserProfile {
  // Datos Personales / Notificaciones (Editables por el usuario)
  telefono: string;
  correoNotificaciones: string;

  // Datos de Identificación (Lectura sola)
  id: number;
  dni: string;
  nombres: string;
  apellidos: string;
  correoCorporativo: string;

  // Datos Organizacionales SST (Lectura sola por ahora)
  sede: string;
  area: string;
  cargo: string;
  fechaIngreso: string;
  
  // Módulos SST futuros (Puntaje/Progreso personal)
  capacitacionesCompletadas?: number;
  capacitacionesPendientes?: number;
}

export interface UpdateProfileRequest {
  telefono: string;
  correoNotificaciones: string;
}