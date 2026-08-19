import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApiResponse } from '../../auth/models/api-response';

import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class resporteService {

  private readonly http = inject(HttpClient);
  private readonly API_URL =  `${environment.apiUrl}/reportes`;


}