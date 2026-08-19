import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

import { DashboardStatsResponse } from '../models/dashboard-stats-response.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private readonly http = inject(HttpClient);

  private readonly API_URL = `${environment.apiUrl}/dashboard`;

  obtenerEstadisticas(): Observable<DashboardStatsResponse> {
    return this.http.get<DashboardStatsResponse>(this.API_URL);
  }
}