import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  Trabajador,
  PaginatedResponse,
  CrearTrabajadorRequest,
  ActualizarTrabajadorRequest,
  MaestraItem,
  CapacitacionItem,
  DocumentoItem
} from '../models/trabajador.model';

@Injectable({
  providedIn: 'root'
})
export class TrabajadorService {

  private readonly http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8080/api/v1/trabajadores';

  // Obtener listado de trabajadores con paginación
  getTrabajadores(page: number = 0, size: number = 10, estado?: string): Observable<PaginatedResponse<Trabajador>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (estado && estado !== 'TODOS') {
      params = params.set('estado', estado);
    }

    return this.http.get<PaginatedResponse<Trabajador>>(this.API_URL, { params });
  }

  // Obtener trabajador por ID
  getTrabajadorById(id: number): Observable<Trabajador> {
    return this.http.get<Trabajador>(`${this.API_URL}/${id}`);
  }

  // Crear nuevo trabajador (Exclusivo Administrador)
  createTrabajador(request: CrearTrabajadorRequest): Observable<Trabajador> {
    return this.http.post<Trabajador>(this.API_URL, request);
  }

  // Actualizar trabajador (Exclusivo Administrador)
  updateTrabajador(id: number, request: ActualizarTrabajadorRequest): Observable<Trabajador> {
    return this.http.put<Trabajador>(`${this.API_URL}/${id}`, request);
  }

  // Cambiar estado ACTIVO/INACTIVO
  changeEstado(id: number, estado: string): Observable<void> {
    const params = new HttpParams().set('estado', estado);
    return this.http.patch<void>(`${this.API_URL}/${id}/estado`, null, { params });
  }

  searchTrabajadores(segment: string, limit: number = 10, soloActivos: boolean = false): Observable<Trabajador[]> {
    let params = new HttpParams()
      .set('segment', segment.trim())
      .set('limit', limit.toString());

    if (soloActivos) {
      params = params.set('estado', 'ACTIVO');
    }

    return this.http.get<Trabajador[]>(`${this.API_URL}/search`, { params });
  }

  // Catálogos
  getSedes(): Observable<MaestraItem[]> {
    return this.http.get<MaestraItem[]>(`${this.API_URL}/catalogos/sedes`);
  }

  getCargos(): Observable<MaestraItem[]> {
    return this.http.get<MaestraItem[]>(`${this.API_URL}/catalogos/cargos`);
  }

  // Capacitaciones mock por trabajador (Para inspección detallada)
  getCapacitacionesByTrabajador(trabajadorId: number): Observable<CapacitacionItem[]> {
    return new Observable(observer => {
      observer.next([
        {
          id: 101,
          titulo: 'Charla de 5 Minutos: Uso correcto de EPP en planta',
          tipo: '5 MINUTOS',
          fecha: '2026-08-01',
          estado: 'APROBADO',
          horas: 1
        },
        {
          id: 102,
          titulo: 'Inducción General de Seguridad e Higiene Ocupacional',
          tipo: 'INDUCCION',
          fecha: '2026-07-15',
          estado: 'APROBADO',
          horas: 8
        },
        {
          id: 103,
          titulo: 'Capacitación: Prevención de Riesgos Ergonómicos',
          tipo: 'CAPACITACION',
          fecha: '2026-08-20',
          estado: 'PENDIENTE',
          horas: 4
        }
      ]);
      observer.complete();
    });
  }

  // Documentos mock por trabajador (Para inspección detallada)
  getDocumentosByTrabajador(trabajadorId: number): Observable<DocumentoItem[]> {
    return new Observable(observer => {
      observer.next([
        {
          id: 201,
          nombre: 'Examen Médico Ocupacional (EMO) 2026',
          categoria: 'EMO',
          fechaEmision: '2026-01-10',
          estado: 'VIGENTE'
        },
        {
          id: 202,
          nombre: 'Constancia de Inducción de Seguridad',
          categoria: 'CERTIFICADO',
          fechaEmision: '2026-07-15',
          estado: 'VIGENTE'
        },
        {
          id: 203,
          nombre: 'Copia DNI / Documento de Identidad',
          categoria: 'IDENTIFICACION',
          fechaEmision: '2025-05-12',
          estado: 'VIGENTE'
        }
      ]);
      observer.complete();
    });
  }
}
