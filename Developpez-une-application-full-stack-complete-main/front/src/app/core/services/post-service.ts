
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Post } from '../interfaces/post.interface';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PostService {

  private http = inject(HttpClient);

  private pathService = '/api/articles';

  /**
   * Récupère le flux d'articles personnalisés (feed)
   * @param sort 'asc' ou 'desc'
   */
  public getFeed(sort: string = 'desc'): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.pathService}/feed?sort=${sort}`);
  }

  /**
   * Récupère tous les articles du site 
   */
  public getAll(): Observable<Post[]> {
    return this.http.get<Post[]>(this.pathService);
  }

  /**
   * Récupère un article précis par son ID
   */
  public getById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.pathService}/${id}`);
  }

  /**
   * Crée un nouvel article
   */
  public create(post: Post): Observable<Post> {
    return this.http.post<Post>(this.pathService, post);
  }
  
  /**
   * Met à jour un article existant
   */
  public update(id: number, post: Post): Observable<Post> {
    return this.http.put<Post>(`${this.pathService}/${id}`, post);
  }

  /**
   * Supprime un article
   */
  public delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.pathService}/${id}`);
  }

}
