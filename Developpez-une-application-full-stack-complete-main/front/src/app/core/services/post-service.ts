
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Post } from '../interfaces/post.interface';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PostService {

  private http = inject(HttpClient);


  /**
   * Récupère le flux d'articles personnalisés (feed)
   * @param sort 'asc' ou 'desc'
   */
  public getFeed(sort: string = 'desc'): Observable<Post[]> {
    return this.http.get<Post[]>(`${environment.postPath}/feed?sort=${sort}`);
  }

  /**
   * Récupère tous les articles du site 
   */
  public getAll(): Observable<Post[]> {
    return this.http.get<Post[]>(environment.postPath);
  }

  /**
   * Récupère un article précis par son ID
   */
  public getById(id: number): Observable<Post> {
    return this.http.get<Post>(`${environment.postPath}/${id}`);
  }

  /**
   * Crée un nouvel article
   */
  public create(post: Post): Observable<Post> {
    return this.http.post<Post>(environment.postPath, post);
  }
  
  /**
   * Met à jour un article existant
   */
  public update(id: number, post: Post): Observable<Post> {
    return this.http.put<Post>(`${environment.postPath}/${id}`, post);
  }

  /**
   * Supprime un article
   */
  public delete(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.postPath}/${id}`);
  }

}
