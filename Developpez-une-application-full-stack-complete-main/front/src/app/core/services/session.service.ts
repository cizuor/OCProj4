import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from 'src/app/core/interfaces/user.interface';


@Injectable({
  providedIn: 'root',
})
export class SessionService {
  // état initial
  public isLogged = false;
  public user: User | undefined;
  
  // il contient la valeur actuelle (false au début)
  private isLoggedSubject = new BehaviorSubject<boolean>(this.isLogged);

  // expose le sujet sous forme d'Observable
  public $isLogged(): Observable<boolean> {
    return this.isLoggedSubject.asObservable();
  }

  // La méthode pour se connecter
  public logIn(user: User): void {
    this.user = user;
    this.isLogged = true;
    this.next(); // On émet le signal de changement
  }

  // La méthode pour se déconnecter
  public logOut(): void {
    localStorage.removeItem('token'); // On nettoie le jeton
    this.user = undefined;
    this.isLogged = false;
    this.next(); // On émet le signal
  }

  // envoyer la mise à jour aux abonnés
  private next(): void {
    this.isLoggedSubject.next(this.isLogged);
  }
}
