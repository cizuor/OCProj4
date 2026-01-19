import { Routes } from "@angular/router";
import { RegisterComponent } from "./features/auth/components/register-component/register-component";
import { LoginComponent } from "./features/auth/components/login-component/login-component";
import { PostListComponent } from "./features/posts/components/post-list-component/post-list-component";
import { PostComponent } from "./features/posts/components/post-component/post-component";
import { PostDetailComponent } from "./features/posts/components/post-detail-component/post-detail-component";
import { UserComponent } from "./features/user/components/user-component/user-component";
import { TopicListComponent } from "./features/topic/components/topic-list-component/topic-list-component";

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
];