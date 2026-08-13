export interface ResponsableInspeccion {
  id: number;
  nombreCompleto: string;
  cargoNombre?: string;
  sedeNombre?: string;
}

export interface Inspeccion {
  id: number;
  tema: string;
  tipo: string;
  fechaInspeccion: string;
  horaInspeccion: string;
  estado: 'PENDIENTE' | 'REALIZADA' | 'RETRASADA' | 'INCUMPLIDA';
  observaciones?: string;
  creadoPor?: string;
  createdAt?: string;
  responsables: ResponsableInspeccion[];
}

export interface CrearInspeccionRequest {
  tema: string;
  tipo: string;
  fechaInspeccion: string;
  horaInspeccion: string;
  responsableIds: number[];
  observaciones?: string;
}
