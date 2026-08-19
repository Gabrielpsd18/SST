import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { DashboardStatsResponse } from '../models/dashboard-stats-response.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private readonly http = inject(HttpClient);

  private readonly API_URL = 'http://localhost:8080/api/v1/dashboard';

  obtenerEstadisticas(): Observable<DashboardStatsResponse> {
    return this.http.get<DashboardStatsResponse>(this.API_URL);
  }
}