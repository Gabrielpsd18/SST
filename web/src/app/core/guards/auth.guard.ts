import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {

  const router = inject(Router);

  const authService = inject(AuthService);

  if (authService.isAuthenticated()) {
    return true;
  }

  if (authService.getToken()) {
    return true;
  }

  router.navigate(['/']);

  return false;

};