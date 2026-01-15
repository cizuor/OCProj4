import { Component, inject } from '@angular/core';
import { SessionService } from './core/services/session.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    standalone: false
})
export class AppComponent {

  private sessionService = inject(SessionService);

  public isLogged$ = this.sessionService.$isLogged();

  public logout(): void {
    this.sessionService.logOut();
  }
}
