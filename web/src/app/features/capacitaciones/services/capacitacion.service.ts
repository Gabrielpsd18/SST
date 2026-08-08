import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  Capacitador,
  CapacitadorRequest,
  Capacitacion,
  CrearCapacitacionRequest
} from '../models/capacitacion.model';
import { PaginatedResponse } from '../../trabajadores/models/trabajador.model';

@Injectable({
  providedIn: 'root'
})
export class CapacitacionService {

  private readonly http = inject(HttpClient);
  private readonly CAPACITACIONES_URL = 'http://localhost:8080/api/v1/capacitaciones';
  private readonly CAPACITADORES_URL = 'http://localhost:8080/api/v1/capacitadores';

  // --- Capacitaciones ---
  getCapacitaciones(page: number = 0, size: number = 10): Observable<PaginatedResponse<Capacitacion>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PaginatedResponse<Capacitacion>>(this.CAPACITACIONES_URL, { params });
  }

  programarCapacitacion(request: CrearCapacitacionRequest): Observable<Capacitacion> {
    return this.http.post<Capacitacion>(this.CAPACITACIONES_URL, request);
  }

  // --- Capacitadores ---
  getCapacitadores(): Observable<Capacitador[]> {
    return this.http.get<Capacitador[]>(this.CAPACITADORES_URL);
  }

  createCapacitador(request: CapacitadorRequest): Observable<Capacitador> {
    return this.http.post<Capacitador>(this.CAPACITADORES_URL, request);
  }

  updateCapacitador(id: number, request: CapacitadorRequest): Observable<Capacitador> {
    return this.http.put<Capacitador>(`${this.CAPACITADORES_URL}/${id}`, request);
  }
}
