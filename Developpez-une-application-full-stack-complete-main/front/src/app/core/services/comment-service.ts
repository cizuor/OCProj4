import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Comment } from '../interfaces/comment.interface';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private http = inject(HttpClient);

  private pathService = '/api/commentaire';

  /**
   * Récupère un commentaire précis par son ID
   */
  public getById(id: number): Observable<Comment> {
    return this.http.get<Comment>(`${this.pathService}/${id}`);
  }


  /**
   * Récupère les commentaire de l'articles 
   */
  public getAllCommentFromPost(idPost: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.pathService}/article/${idPost}`);
  }

  
  /**
   * Crée un nouveau commentaire
   */
  public create(comment: Comment, idPost: number) : Observable<Comment> {
    return this.http.post<Comment>(`${this.pathService}/article/${idPost}`, comment);
  }
}
