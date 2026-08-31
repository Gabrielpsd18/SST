import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface DocumentoGeneral {
  id: number;
  title: string;
  categoria: string;
  description?: string | null;
  filePath?: string | null;
  version?: string;
  createdBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface DocumentoPersonal {
  id: number;
  userId: number;
  tipo: string;
  filePath?: string | null;
  issueDate?: string;
  expirationDate?: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DocumentosService {
  private readonly http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/documentos`;

  getGenerales(): Observable<DocumentoGeneral[]> {
    return this.http.get<DocumentoGeneral[]>(`${this.API_URL}/generales`);
  }

  getPersonales(): Observable<DocumentoPersonal[]> {
    return this.http.get<DocumentoPersonal[]>(`${this.API_URL}/personales`);
  }

  uploadGeneral(
    file: File,
    title: string,
    category: string,
    description?: string,
    version: string = '1.0'
  ): Observable<DocumentoGeneral> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    formData.append('category', category);
    if (description) {
      formData.append('description', description);
    }
    formData.append('version', version);

    return this.http.post<DocumentoGeneral>(`${this.API_URL}/generales`, formData);
  }

  uploadPersonal(
    file: File,
    tipo: string,
    issueDate: string,
    expirationDate: string,
    userId?: number
  ): Observable<DocumentoPersonal> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('tipo', tipo);
    formData.append('issueDate', issueDate);
    formData.append('expirationDate', expirationDate);
    if (userId != null) {
      formData.append('userId', String(userId));
    }

    return this.http.post<DocumentoPersonal>(`${this.API_URL}/personales`, formData);
  }
}
