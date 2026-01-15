import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { TopicComponent } from './topic/topic.component';
import { jwtInterceptor } from './interceptors/jwt-interceptor';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    TopicComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [
    provideHttpClient(
      withInterceptors([jwtInterceptor])
    )],
  bootstrap: [AppComponent]
})
export class AppModule { }
