import { Component, computed, inject, OnInit, output, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../features/auth/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  menuClick = output<void>();

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected userName = signal<string>('Usuario');
  protected userRole = signal<string>('');

  protected userInitials = computed(() => {
    const names = this.userName().trim().split(' ');
    if (names.length >= 2) {
      return `${names[0][0]}${names[1][0]}`.toUpperCase();
    }
    return names[0] ? names[0].substring(0, 2).toUpperCase() : 'US';
  });

  ngOnInit(): void {
    this.userName.set(this.authService.getUserName());
    const role = localStorage.getItem('user_role');
    if (role) {
      this.userRole.set(role);
    }
  }
  goToProfile(): void {
    this.router.navigate(['/profile']);
  }
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}