import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

import { LoginComponent } from './features/auth/login/login.component';
import { CallbackComponent } from './features/auth/callback/callback.component';
import { RepoListComponent } from './features/repos/repo-list/repo-list.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'auth/callback', component: CallbackComponent },

  { path: 'repos', component: RepoListComponent, canActivate: [authGuard] },

  { path: '', redirectTo: 'repos', pathMatch: 'full' },
  { path: '**', redirectTo: 'repos' }
];
