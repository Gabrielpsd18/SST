import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { LoginRequest } from '../models/login-request';
import { JwtResponse } from '../models/jwt-response';
import { ApiResponse } from '../models/api-response';
import { STORAGE } from '../../../core/constants/storage.constants';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/auth`;

  login(request: LoginRequest): Observable<ApiResponse<JwtResponse>> {
    return this.http.post<ApiResponse<JwtResponse>>(`${this.API_URL}/login`, request)
      .pipe(
        tap(response => {
          if (response.data) {
            this.saveSessionData(response.data);
          }
        })
      );
  }

  private saveSessionData(data: JwtResponse): void {
    localStorage.setItem(STORAGE.ACCESS_TOKEN, data.accessToken);
    localStorage.setItem(STORAGE.USER_NAME, data.nombreCompleto);
    localStorage.setItem(STORAGE.USER_EMAIL, data.email);
    localStorage.setItem(STORAGE.USER_ROLE, data.role);
  }

  getUserName(): string {
    return localStorage.getItem(STORAGE.USER_NAME) || 'Usuario';
  }

  getUserRole(): string | null {
    return localStorage.getItem(STORAGE.USER_ROLE);
  }

  getToken(): string | null {
    return localStorage.getItem(STORAGE.ACCESS_TOKEN);
  }

  logout(): void {
    localStorage.removeItem(STORAGE.ACCESS_TOKEN);
    localStorage.removeItem(STORAGE.USER_NAME);
    localStorage.removeItem(STORAGE.USER_EMAIL);
    localStorage.removeItem(STORAGE.USER_ROLE);
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }
}