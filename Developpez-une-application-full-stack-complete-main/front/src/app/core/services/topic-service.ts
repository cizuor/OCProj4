import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Topic } from '../interfaces/topic.interface';
import { UserService } from './user-service';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TopicService {
  private http = inject(HttpClient);



  /**
   * Récupère un topic précis par son ID
   */
  public getById(id: number): Observable<Topic>{
    return this.http.get<Topic>(`${environment.topicPath}/${id}`);
  }

  /**
   * Récupère tous les topic du site 
   */
  public getAll():Observable<Topic[]>{
    return this.http.get<Topic[]>(environment.topicPath);
  }

  /**
   * Récupère tous les topic suivie par l'utilisateur  
   */
  public getFolowed(): Observable<Topic[]> {
    return this.http.get<Topic[]>(`${environment.topicPath}/suivie`);
  }

   /**
   * Crée un nouveau theme
   */
  public create(topic: Topic): Observable<Topic> {
    return this.http.post<Topic>(environment.topicPath, topic);
  }

  /**
   * S'abonner à un thème
   * Renvoie la liste mise à jour des thèmes (comme configuré dans ton Java)
   */
  public subscribe(topicId: number): Observable<Topic[]> {
    return this.http.post<Topic[]>(`${environment.userPath}/subscribe/${topicId}`, {});
  }

  /**
   * Se désabonner d'un thème
   */
  public unsubscribe(topicId: number): Observable<Topic[]> {
    return this.http.post<Topic[]>(`${environment.userPath}/unsubscribe/${topicId}`, {});
  }
}
