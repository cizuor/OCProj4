import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PostComment } from '../interfaces/comment.interface';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private http = inject(HttpClient);


  /**
   * Récupère un commentaire précis par son ID
   */
  public getById(id: number): Observable<PostComment> {
    return this.http.get<PostComment>(`${environment.commentPath}/${id}`);
  }


  /**
   * Récupère les commentaire de l'articles 
   */
  public getAllCommentFromPost(idPost: number): Observable<PostComment[]> {
    return this.http.get<PostComment[]>(`${environment.commentPath}/article/${idPost}`);
  }


  /**
   * Crée un nouveau commentaire
   */
  public create(comment: PostComment, idPost: number) : Observable<PostComment> {
    return this.http.post<PostComment>(`${environment.commentPath}/article/${idPost}`, comment);
  }
}
