import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ImportacionService {
  private readonly http = inject(HttpClient);
  private readonly API = 'http://localhost:8080/api/v1/importaciones';

  upload(file: File, month: string = 'THIS'): Observable<any>{
    const fd = new FormData();
    fd.append('file', file);
    const params = new HttpParams()
      .set('month', month);
    return this.http.post<any>(`${this.API}/trabajadores`, fd, { params });
  }

  apply(batchId: number): Observable<any>{
    return this.http.post<any>(`${this.API}/${batchId}/apply`, null);
  }

  listIssues(batchId: number){
    return this.http.get<any[]>(`${this.API}/${batchId}/issues`);
  }

  resolveIssue(batchId: number, issueId: number, action: string){
    return this.http.post(`${this.API}/${batchId}/issues/${issueId}/resolve`, { action });
  }
}
export interface TrabajadorImportDTO {
  documento: string;
  nombres: string;
  apellidos: string;
  // ... los campos que tenga tu Excel
}