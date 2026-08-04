import { Routes } from '@angular/router';

import { LoginComponent } from './core/pages/login/login.component';
import { HomeComponent } from './core/pages/home/home.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [

  {
    path: '',
    component: LoginComponent
  },

  {
    path:'home',
    component:HomeComponent,
    canActivate:[authGuard]
  },

  {
    path: '**',
    redirectTo: ''
  }

];