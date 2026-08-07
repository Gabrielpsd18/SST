import { Component, inject, input, OnInit, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import {
  LayoutDashboard,
  Users,
  GraduationCap,
  FileText,
  ChartColumn,
  Shield,
  LucideAngularModule,
  LucideIconData,
} from 'lucide-angular';
import { MENU_ITEMS } from '../../constants/menu-items';
import { AuthService } from '../../../../features/auth/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, LucideAngularModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent implements OnInit {

  public readonly collapsed = input<boolean>(false);
  private readonly authService = inject(AuthService);

  protected readonly menuItems = MENU_ITEMS;
  protected readonly Shield = Shield;

  protected userName = signal<string>('Usuario');
  protected userRole = signal<string>('Rol');

  protected readonly icons: Record<string, LucideIconData> = {
    'layout-dashboard': LayoutDashboard,
    'users': Users,
    'graduation-cap': GraduationCap,
    'file-text': FileText,
    'chart-column': ChartColumn
  };
  ngOnInit(): void {
    this.userName.set(this.authService.getUserName());
    const role = localStorage.getItem('user_role');
    if (role) {
      this.userRole.set(role);
    }
  }
}