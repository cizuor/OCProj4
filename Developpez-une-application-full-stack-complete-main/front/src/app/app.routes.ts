import { Routes } from "@angular/router";
import { RegisterComponent } from "./features/auth/components/register-component/register-component";
import { LoginComponent } from "./features/auth/components/login-component/login-component";
import { PostListComponent } from "./features/posts/components/post-list-component/post-list-component";
import { PostComponent } from "./features/posts/components/post-component/post-component";
import { PostDetailComponent } from "./features/posts/components/post-detail-component/post-detail-component";
import { UserComponent } from "./features/user/components/user-component/user-component";
import { TopicListComponent } from "./features/topic/components/topic-list-component/topic-list-component";
import { AuthLayout } from "./layouts/auth-layout/auth-layout";
import { MainLayout } from "./layouts/main-layout/main-layout";
import { authGuard } from "./core/gards/auth.guard";
import { AccueilComponent } from "./features/accueil/components/accueil-component/accueil-component";

export const routes: Routes = [
  {
    path: '',
    component: AccueilComponent,
    pathMatch: 'full' 
  },
  // groupe authlayout (image)
  {
    path: '',
    component: AuthLayout,
    children: [
    {
      path: 'register',
      component: RegisterComponent
    },
    {
      path: 'login',
      component: LoginComponent
    }
    ]
  },
  // groupe autre
  {
  path: '',
  component: MainLayout, // Un autre composant avec une barre de menu différente
  canActivate: [authGuard],
  children: [
    {
      path: 'profil',
      component: UserComponent
    },
    {
      path: 'themes',
      component: TopicListComponent
    },
    {
      path: 'articles', 
      children: [
        {
          path: '',
          component: PostListComponent,
          data: { title: 'articles' },
        },
        {
          path: 'create',
          component: PostComponent,
          data: { title: 'articles - create' },
        },
        {
          path: ':id',
          component: PostDetailComponent,
          data: { title: 'article' },
        },
      ]
    }
    ]
  }
];