import { Component, inject, output } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../../features/auth/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  menuClick = output<void>();
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  logout(): void {

    this.authService.logout();

    this.router.navigate(['/login']);

  }
}