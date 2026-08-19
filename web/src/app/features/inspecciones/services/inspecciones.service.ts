import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { PaginatedResponse } from '../../trabajadores/models/trabajador.model';
import { CrearInspeccionRequest, Inspeccion } from '../models/inspeccion.model';
import { environment } from '../../../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class InspeccionesService {
  private readonly http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/inspecciones`;
  
  getInspecciones(page: number = 0, size: number = 10): Observable<PaginatedResponse<Inspeccion>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedResponse<Inspeccion>>(this.API_URL, { params });
  }

  getInspeccionById(id: number): Observable<Inspeccion> {
    return this.http.get<Inspeccion>(`${this.API_URL}/${id}`);
  }

  createInspeccion(request: CrearInspeccionRequest): Observable<Inspeccion> {
    return this.http.post<Inspeccion>(this.API_URL, request);
  }

  updateInspeccion(id: number, request: CrearInspeccionRequest): Observable<Inspeccion> {
    return this.http.put<Inspeccion>(`${this.API_URL}/${id}`, request);
  }

  changeEstado(id: number, estado: string): Observable<void> {
    const params = new HttpParams().set('estado', estado);
    return this.http.patch<void>(`${this.API_URL}/${id}/estado`, null, { params });
  }
}
