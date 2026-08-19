import { Component, inject, input, OnInit, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import {
  LucideLayoutDashboard,
  LucideUsers,
  LucideGraduationCap,
  LucideFileText,
  LucideChartColumn,
  LucideShield,
  LucideClipboardList,
} from '@lucide/angular';
import { MENU_ITEMS } from '../../constants/menu-items';
import { AuthService } from '../../../../features/auth/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    RouterLink, 
    RouterLinkActive,
    LucideLayoutDashboard,
    LucideUsers,
    LucideGraduationCap,
    LucideFileText,
    LucideChartColumn,
    LucideShield,
    LucideClipboardList
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent implements OnInit {

  public readonly collapsed = input<boolean>(false);
  private readonly authService = inject(AuthService);

  protected readonly menuItems = MENU_ITEMS;

  protected userName = signal<string>('Usuario');
  protected userRole = signal<string>('Rol');

  ngOnInit(): void {
    this.userName.set(this.authService.getUserName());
    const role = localStorage.getItem('user_role');
    if (role) {
      this.userRole.set(role);
    }
  }
}