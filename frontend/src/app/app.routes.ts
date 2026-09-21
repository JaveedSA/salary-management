import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import { EmployeeWorkspaceComponent } from './employees/employee-workspace.component';
import { ImportWorkspaceComponent } from './imports/import-workspace.component';

export const routes: Routes = [
	{ path: 'login', component: LoginComponent },
	{ path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
	{ path: 'employees', component: EmployeeWorkspaceComponent, canActivate: [authGuard] },
	{ path: 'imports', component: ImportWorkspaceComponent, canActivate: [authGuard] },
	{ path: '', pathMatch: 'full', redirectTo: 'dashboard' },
	{ path: '**', redirectTo: 'dashboard' }
];
