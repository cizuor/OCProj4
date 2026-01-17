import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../interfaces/user.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { JwtResponse } from '../interfaces/jwtResponse.interface';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
   private http = inject(HttpClient);


   public register(registerRequest: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${environment.authPath}/register`, registerRequest);
  }

  public login(loginRequest: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${environment.authPath}/login`, loginRequest);
  }

  public me(): Observable<User> {
    return this.http.get<User>(`${environment.userPath}/me`);
  }
}
