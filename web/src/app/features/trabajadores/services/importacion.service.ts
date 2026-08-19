import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ImportacionService {
  private readonly http = inject(HttpClient);
  private readonly API = `${environment.apiUrl}/importaciones`;;

  // Sube el archivo y ejecuta la importación e implementación directa de golpe
  uploadAndImplement(file: File, month: string = 'THIS'): Observable<any> {
    const fd = new FormData();
    fd.append('file', file);
    const params = new HttpParams().set('month', month);
    return this.http.post<any>(`${this.API}/trabajadores`, fd, { params });
  }

  // Permite reenviar una fila individual corregida desde la tabla interactiva de errores
  retryRow(rowPayload: any): Observable<any> {
    return this.http.post<any>(`${this.API}/trabajadores/retry-row`, rowPayload);
  }

  // Obtiene la lista de errores/incidencias guardados en la base de datos
  getPendingErrors(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API}/trabajadores/errors`);
  }

  // Descarta un error de la base de datos por ID
  deleteError(id: number): Observable<any> {
    return this.http.delete<any>(`${this.API}/trabajadores/errors/${id}`);
  }
}

// Estructura adaptada para los datos del trabajador y los errores con campos limpios
export interface InvalidRowDetail {
  id?: number;
  dni: string;
  trabajador: string;
  telefono: string;
  sede: string;
  cargo: string;
  errorMessage: string;
}