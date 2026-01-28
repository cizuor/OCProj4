import { TestBed } from '@angular/core/testing';

import { TopicService } from './topic-service';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Topic } from '../interfaces/topic.interface';
import { environment } from 'src/environments/environment';

describe('TopicService', () => {
  let service: TopicService;
  let httpMock: HttpTestingController;

  const expectJasmine = (expect as any);

  const mockTopics: Topic[] = [
    { id: 1, title: 'Java', description: 'Description Java', liked: false },
    { id: 2, title: 'Angular', description: 'Description Angular', liked: true }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(TopicService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Vérifie qu'il n'y a pas de requêtes HTTP orphelines
    httpMock.verify();
  });

  it('should be created', () => {
    expectJasmine(service).toBeTruthy();
  });

  it('should fetch all topics', () => {
    service.getAll().subscribe(topics => {
      expectJasmine(topics).toEqual(mockTopics);
    });

    const req = httpMock.expectOne(environment.topicPath);
    expectJasmine(req.request.method).toBe('GET');
    req.flush(mockTopics);
  });

  it('should fetch followed topics', () => {
    service.getFolowed().subscribe(topics => {
      expectJasmine(topics.length).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.topicPath}/suivie`);
    expectJasmine(req.request.method).toBe('GET');
    req.flush([mockTopics[1]]);
  });

  it('should fetch a topic by ID', () => {
    service.getById(1).subscribe(topic => {
      expectJasmine(topic.title).toBe('Java');
    });

    const req = httpMock.expectOne(`${environment.topicPath}/1`);
    expectJasmine(req.request.method).toBe('GET');
    req.flush(mockTopics[0]);
  });

  it('should create a new topic', () => {
    const newTopic: Topic = { id : 3, title: 'C#', description: 'Desc', liked: false };
    
    service.create(newTopic).subscribe(topic => {
      expectJasmine(topic.title).toBe('C#');
    });

    const req = httpMock.expectOne(environment.topicPath);
    expectJasmine(req.request.method).toBe('POST');
    req.flush({ ...newTopic, id: 3 });
  });

  it('should subscribe to a topic using userPath', () => {
    service.subscribe(1).subscribe(topics => {
      expectJasmine(topics).toBeTruthy();
    });

    // On vérifie que c'est bien l'URL de l'utilisateur qui est appelée
    const req = httpMock.expectOne(`${environment.userPath}/subscribe/1`);
    expectJasmine(req.request.method).toBe('POST');
    expectJasmine(req.request.body).toEqual({}); // Le body doit être vide comme dans ton code
    req.flush(mockTopics);
  });

  it('should unsubscribe from a topic using userPath', () => {
    service.unsubscribe(1).subscribe(topics => {
      expectJasmine(topics).toBeTruthy();
    });

    const req = httpMock.expectOne(`${environment.userPath}/unsubscribe/1`);
    expectJasmine(req.request.method).toBe('POST');
    req.flush(mockTopics);
  });
});
