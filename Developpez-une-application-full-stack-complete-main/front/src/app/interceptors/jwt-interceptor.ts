import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // recup le token
   const token = localStorage.getItem('token');

   // si existe on ajoute le header a la requette 
    if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }
  // sinon en envoi la requete de base
  return next(req);
};
