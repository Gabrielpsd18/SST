import { Component, input } from '@angular/core';
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

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, LucideAngularModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {

  public readonly collapsed = input<boolean>(false);

  protected readonly menuItems = MENU_ITEMS;
  protected readonly Shield = Shield;

  protected readonly icons: Record<string, LucideIconData> = {
    'layout-dashboard': LayoutDashboard,
    'users': Users,
    'graduation-cap': GraduationCap,
    'file-text': FileText,
    'chart-column': ChartColumn
  };
}