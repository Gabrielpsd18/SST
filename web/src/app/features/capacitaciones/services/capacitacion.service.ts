import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

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

  // Get a single capacitacion by id (used for editing)
  getCapacitacionById(id: number): Observable<any> {
    return this.http.get<any>(`${this.CAPACITACIONES_URL}/${id}`);
  }

  // Update an existing capacitacion
  updateCapacitacion(id: number, request: CrearCapacitacionRequest): Observable<Capacitacion> {
    return this.http.put<Capacitacion>(`${this.CAPACITACIONES_URL}/${id}`, request);
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

  // --- Trabajadores Asignados ---
    // The backend currently returns capacitacion details (including links and capacitadores)
    // via GET /capacitaciones/{id}. If a list of trabajadores is not included, this
    // method will return an empty array by default. If in the future the backend
    // includes a 'trabajadores' array, this will map it.
    getTrabajadoresAsignados(capacitacionId: number): Observable<any[]> {
      return this.getCapacitacionById(capacitacionId).pipe(
        map(resp => resp && resp.trabajadores ? resp.trabajadores : [])
      );
    }

    // --- Videos ---
    // Maps to response.linksVideo from the single GET
    getVideosCapacitacion(capacitacionId: number): Observable<string[]> {
      return this.getCapacitacionById(capacitacionId).pipe(
        map(resp => resp && resp.linksVideo ? resp.linksVideo : [])
      );
    }

    // --- Evaluaciones ---
    // Maps to response.linksEvaluacion from the single GET
    getEvaluacionesCapacitacion(capacitacionId: number): Observable<string[]> {
      return this.getCapacitacionById(capacitacionId).pipe(
        map(resp => resp && resp.linksEvaluacion ? resp.linksEvaluacion : [])
      );
    }
}
