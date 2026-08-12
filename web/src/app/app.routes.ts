import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/pages/login/login.component';
import { HomeComponent } from './features/dashboard/pages/home/home.component';
import { ProfileComponent } from './features/profile/pages/profile/profile.component';
import { TrabajadoresListComponent } from './features/trabajadores/pages/trabajadores-list/trabajadores-list.component';
import { CapacitacionesComponent } from './features/capacitaciones/pages/capacitaciones/capacitaciones.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { authGuard } from './features/auth/guards/auth.guard';
import { TrabajadoresCreateComponent } from './features/trabajadores/pages/trabajadores-create/trabajadores-create.component';
import { TrabajadoresImportComponent } from './features/trabajadores/pages/trabajadores-import/trabajadores-import.component';
import { InspeccionesListComponent } from './features/inspecciones/pages/inspecciones-list/inspecciones-list.component';
import { DocumentosMainComponent } from './features/documentos/pages/documentos-main/documentos-main.component';
import { ReportesMainComponent } from './features/reportes/pages/reportes-main/reportes-main.component';
export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'home',
        component: HomeComponent
      },
      {
        path: 'profile',
        component: ProfileComponent
      },
      {
        path: 'trabajadores',
        children: [
          {
            path: '',
            component: TrabajadoresListComponent
          },
          {
            path: 'nuevo',
            component: TrabajadoresCreateComponent
          },
          {
            path: 'importar',
            component: TrabajadoresImportComponent
          }
        ]
      },
      {
        path: 'capacitaciones',
        component: CapacitacionesComponent
      },
      {
        path: 'inspecciones',
        component: InspeccionesListComponent
      },
      {
        path: 'documentos',
        component: DocumentosMainComponent
      },
      {
        path: 'reportes',
        component: ReportesMainComponent
      },
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];