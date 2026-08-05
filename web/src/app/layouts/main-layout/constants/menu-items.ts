import { MenuItem } from '../models/menu-item.model';

export const MENU_ITEMS: MenuItem[] = [
  {
    label: 'Dashboard',
    icon: 'layout-dashboard',
    route: '/home'
  },
  {
    label: 'Trabajadores',
    icon: 'users',
    route: '/trabajadores'
  },
  {
    label: 'Capacitaciones',
    icon: 'graduation-cap',
    route: '/capacitaciones'
  },
  {
    label: 'Documentos',
    icon: 'file-text',
    route: '/documentos'
  },
  {
    label: 'Estadísticas',
    icon: 'chart-column',
    route: '/estadisticas'
  }
];