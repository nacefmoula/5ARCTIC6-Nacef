import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { EntreprisesComponent } from './pages/entreprises/entreprises';
import { EquipesComponent } from './pages/equipes/equipes';
import { ProjetsComponent } from './pages/projets/projets';
import { ProjetsDetaillesComponent } from './pages/projets-detailles/projets-detailles';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'entreprises', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'entreprises',       component: EntreprisesComponent, canActivate: [authGuard] },
  { path: 'equipes',           component: EquipesComponent, canActivate: [authGuard] },
  { path: 'projets',           component: ProjetsComponent, canActivate: [authGuard] },
  { path: 'projets-detailles', component: ProjetsDetaillesComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'entreprises' }
];
