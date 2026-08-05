import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { LoginRequest } from '../models/login-request';
import { JwtResponse } from '../models/jwt-response';
import { ApiResponse } from '../models/api-response';
import { STORAGE } from '../../../core/constants/storage.constants';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private http = inject(HttpClient);

  private readonly API_URL = 'http://localhost:8080/api/v1/auth';

  login(request: LoginRequest): Observable<ApiResponse<JwtResponse>> {

    return this.http.post<ApiResponse<JwtResponse>>(
      `${this.API_URL}/login`,
      request
    );

  }
  saveToken(token: string): void {
    localStorage.setItem(STORAGE.ACCESS_TOKEN, token);
  }

  getToken(): string | null {
    return localStorage.getItem(STORAGE.ACCESS_TOKEN);
  }

  removeToken(): void {
    localStorage.removeItem(STORAGE.ACCESS_TOKEN);
  }

  logout(): void {
    this.removeToken();
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

}