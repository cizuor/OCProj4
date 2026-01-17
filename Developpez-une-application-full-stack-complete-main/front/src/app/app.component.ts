import { Component, inject, OnInit } from '@angular/core';
import { SessionService } from './core/services/session.service';
import { Observable } from 'rxjs';
import { AuthService } from './core/services/auth.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    standalone: false
})
export class AppComponent implements OnInit {

  private sessionService = inject(SessionService);
  private authService = inject(AuthService)

  public isLogged$ = this.sessionService.$isLogged();

  public logout(): void {
    this.sessionService.logOut();
  }

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      this.authService.me().subscribe({
        next: (user) => this.sessionService.logIn(user),
        error: () => this.sessionService.logOut() 
      });
    }
  }
}
