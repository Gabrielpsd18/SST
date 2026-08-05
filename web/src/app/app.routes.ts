import { Routes } from '@angular/router';

import { LoginComponent } from './features/auth/pages/login/login.component';
import { HomeComponent } from './features/auth/pages/home/home.component';

import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';

import { authGuard } from './features/auth/guards/auth.guard';

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
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      }

    ]
  },

  {
    path: '**',
    redirectTo: ''
  }

];