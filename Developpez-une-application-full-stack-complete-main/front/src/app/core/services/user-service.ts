import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { User } from '../interfaces/user.interface';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);



  /**
   * Récupère un article précis par son ID
   */
  public getById(id: number): Observable<User> {
    return this.http.get<User>(`${environment.userPath}/${id}`);
  }

  /**
   * Récupère un article précis par son ID
   */
  public getMe(): Observable<User> {
    return this.http.get<User>(`${environment.userPath}/me`);
  }

  // put /me
}
