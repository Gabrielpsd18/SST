import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApiResponse } from '../../auth/models/api-response';
import { UserProfile, UpdateProfileRequest } from '../models/user-profile.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8080/api/v1/users';

  // Obtener la información del perfil del usuario logueado
  getProfile(): Observable<ApiResponse<UserProfile>> {
    return this.http.get<ApiResponse<UserProfile>>(`${this.API_URL}/me`);
  }

  // Actualizar solo los campos de notificación/teléfono
  updateProfile(request: UpdateProfileRequest): Observable<ApiResponse<UserProfile>> {
    return this.http.patch<ApiResponse<UserProfile>>(`${this.API_URL}/me`, request);
  }
}