import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PostListComponent } from './post-list-component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { PostService } from 'src/app/core/services/post-service';
import { TopicService } from 'src/app/core/services/topic-service'; // Importe le 2ème service
import { of } from 'rxjs'; // Pour simuler les retours d'API

describe('PostListComponent', () => {
  let component: PostListComponent;
  let fixture: ComponentFixture<PostListComponent>;

  // 1. Création des Mocks (Faux services)
  // On simule getFeed pour qu'il renvoie une liste vide au lieu de faire un appel HTTP
  const mockPostService = {
    getFeed: () => of([]) 
  };

  const mockTopicService = {
    getUserSubscriptions: () => of([])
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]), 
        // 2. On utilise les Mocks ici
        { provide: PostService, useValue: mockPostService },
        { provide: TopicService, useValue: mockTopicService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});