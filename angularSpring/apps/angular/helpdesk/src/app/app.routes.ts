import {Routes, RouterModule} from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/security/login/login.component';
import { ModuleWithProviders } from '@angular/core';

export const ROUTES: Routes = [
    { path : '', component: HomeComponent},
    { path : 'login', component: LoginComponent},
    { path : 'home', component: HomeComponent}
]

export const routes: ModuleWithProviders = RouterModule.forRoot(ROUTES);