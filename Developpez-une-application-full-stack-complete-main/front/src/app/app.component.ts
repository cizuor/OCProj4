import { Component, inject, OnInit } from '@angular/core';
import { SessionService } from './core/services/session.service';
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
    this.autoLogin()
  }


  private autoLogin(): void {
    const token = localStorage.getItem('token');
    
    if (token) {
      // On appelle ta route /me pour récupérer les infos de l'utilisateur
      this.authService.me().subscribe({
        next: (user) => {
          // On restaure la session dans le service
          this.sessionService.logIn(user);
        },
        error: () => {
          // Si le token est expiré ou invalide, on nettoie tout
          this.sessionService.logOut();
        }
      });
    }
  }
}
