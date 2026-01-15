import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../interfaces/user.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { JwtResponse } from '../interfaces/jwtResponse.interface';
import { LoginRequest } from '../interfaces/loginRequest.interface';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
   private http = inject(HttpClient);
   private pathService = '/api/auth';


   public register(registerRequest: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.pathService}/register`, registerRequest);
  }

  public login(loginRequest: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.pathService}/login`, loginRequest);
  }

  public me(): Observable<User> {
    return this.http.get<User>(`/api/utilisateur/me`);
  }
}
