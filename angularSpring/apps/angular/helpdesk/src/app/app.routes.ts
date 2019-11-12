import {Routes, RouterModule} from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/security/login/login.component';
import { ModuleWithProviders } from '@angular/core';
import { AuthGuard } from './components/security/auth.guard';
import { UserNewComponent } from './components/user-new/user-new.component';
import { AuthInterceptor } from './components/security/auth.interceptor';
import { UserListComponent } from './components/user-list/user-list.component';

export const ROUTES: Routes = [
    { path : '', component: HomeComponent,canActivate: [AuthGuard]},
    { path : 'login', component: LoginComponent},
    { path : 'home', component: HomeComponent, canActivate: [AuthGuard]},
    { path : 'user-new', component: UserNewComponent, canActivate: [AuthGuard]},
    { path : 'user-list', component: UserListComponent, canActivate: [AuthGuard]}
]

export const routes: ModuleWithProviders = RouterModule.forRoot(ROUTES);