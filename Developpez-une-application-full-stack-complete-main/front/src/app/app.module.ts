import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { jwtInterceptor } from './interceptors/jwt-interceptor';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes) 
  ],
  providers: [
    provideHttpClient(
      withInterceptors([jwtInterceptor])
    )],
  bootstrap: [AppComponent]
})
export class AppModule { }
