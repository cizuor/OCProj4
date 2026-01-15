import { Routes } from "@angular/router";
import { RegisterComponent } from "./features/auth/components/register-component/register-component";
import { LoginComponent } from "./features/auth/components/login-component/login-component";

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
];