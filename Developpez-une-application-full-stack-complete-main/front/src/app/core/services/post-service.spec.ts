import { TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { PostService } from './post-service';
import { Post } from '../interfaces/post.interface';

import { environment } from 'src/environments/environment';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;

  const expectJasmine = (expect as any);

   const mockPosts: Post[] = [
    { id: 1, titre: 'Titre 1', contenu: 'Contenu 1', topicId: 1,topicName: 'Java',userName: 'Admin',userID: 1, createdAt: '' },
    { id: 2, titre: 'Titre 2', contenu: 'Contenu 2', topicId: 2,topicName: 'Angular',userName: 'Admin', userID: 1, createdAt: '' }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [
        PostService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ]});
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Vérifie qu'il n'y a pas de requêtes HTTP qui n'ont pas été traitées
    httpMock.verify();
  });

  it('should be created', () => {
    expectJasmine(service).toBeTruthy();
  });


  it('should fetch feed articles with sort parameter', () => {
    const sort = 'asc';
    service.getFeed(sort).subscribe(posts => {
      expectJasmine(posts).toEqual(mockPosts);
    });

    const req = httpMock.expectOne(`${environment.postPath}/feed?sort=${sort}`);
    expectJasmine(req.request.method).toBe('GET');
    req.flush(mockPosts); // Simule la réponse du serveur
  });

  it('should fetch all articles', () => {
    service.getAll().subscribe(posts => {
      expectJasmine(posts.length).toBe(2);
    });

    const req = httpMock.expectOne(environment.postPath);
    expectJasmine(req.request.method).toBe('GET');
    req.flush(mockPosts);
  });

  it('should fetch a single article by ID', () => {
    const mockId = 1;
    service.getById(mockId).subscribe(post => {
      expectJasmine(post.titre).toBe('Titre 1');
    });

    const req = httpMock.expectOne(`${environment.postPath}/${mockId}`);
    expectJasmine(req.request.method).toBe('GET');
    req.flush(mockPosts[0]);
  });

  it('should create a new article via POST', () => {
    const newPost: Post = {id: 3, titre: 'Nouveau', contenu: 'Contenu', topicId: 1, topicName: 'Java',userName: 'Admin',userID: 1, createdAt: '' };
    
    service.create(newPost).subscribe(post => {
      expectJasmine(post).toEqual({ ...newPost, id: 3 });
    });

    const req = httpMock.expectOne(environment.postPath);
    expectJasmine(req.request.method).toBe('POST');
    expectJasmine(req.request.body).toEqual(newPost);
    req.flush({ ...newPost, id: 3 });
  });

  it('should update an article via PUT', () => {
    const updatedPost: Post = { id: 1, titre: 'Titre Modifié', contenu: 'Contenu', topicId: 1, topicName: 'Java',userName: 'Admin',userID: 1, createdAt: '' };
    
    service.update(1, updatedPost).subscribe(post => {
      expectJasmine(post.titre).toBe('Titre Modifié');
    });

    const req = httpMock.expectOne(`${environment.postPath}/1`);
    expectJasmine(req.request.method).toBe('PUT');
    req.flush(updatedPost);
  });

  it('should delete an article via DELETE', () => {
    service.delete(1).subscribe();

    const req = httpMock.expectOne(`${environment.postPath}/1`);
    expectJasmine(req.request.method).toBe('DELETE');
    req.flush(null); // Réponse vide pour un DELETE
  });
});
