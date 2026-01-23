import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { SessionService } from '../services/session.service';

export const authGuard: CanActivateFn = () => {
  const sessionService = inject(SessionService);
  const router = inject(Router);

  // 1. On regarde dans le SessionService si l'user est logué
  if (sessionService.isLogged) {
    return true; // Accès autorisé
  }
  // 2. Si on vient de rafraîchir la page (F5), on vérifie le localStorage
  const token = localStorage.getItem('token');
  if (token) {
    // On laisse passer. AppComponent s'occupera de re-remplir 
    // le SessionService en arrière-plan.
    return true; 
  }

  // 3. Sinon, on le redirige vers la page de connexion
  router.navigate(['']);
  return false; // Accès refusé
};