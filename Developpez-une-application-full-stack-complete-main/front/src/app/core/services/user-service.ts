import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { User } from '../interfaces/user.interface';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { UpdateRequest } from '../interfaces/updateRequest.interface';

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

  public update(req: UpdateRequest):Observable<User> {
    return this.http.put<User>(`${environment.userPath}/me`, req);
  }
}
