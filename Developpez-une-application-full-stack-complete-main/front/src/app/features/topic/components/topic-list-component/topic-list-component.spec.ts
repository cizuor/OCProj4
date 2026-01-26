/// <reference types="jasmine" />

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopicListComponent } from './topic-list-component';
import { provideRouter } from '@angular/router';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Topic } from 'src/app/core/interfaces/topic.interface';
import { of } from 'rxjs';
import { TopicCardComponent } from '../topic-card-component/topic-card-component';
import { CommonModule } from '@angular/common';
import { TopicService } from 'src/app/core/services/topic-service';

describe('TopicListComponent', () => {
  let component: TopicListComponent;
  let fixture: ComponentFixture<TopicListComponent>;

  const mockTopics: Topic[] = [
    { id: 1, title: 'Java', description: 'Desc Java', liked: false },
    { id: 2, title: 'Angular', description: 'Desc Angular', liked: true }
  ];

  const mockTopicService = {
    getAll: jasmine.createSpy('getAll').and.returnValue(of(mockTopics)),
    subscribe: jasmine.createSpy('subscribe').and.returnValue(of([]))
  };



  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopicListComponent, TopicCardComponent, CommonModule],
       providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
         { provide: TopicService, useValue: mockTopicService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TopicListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

   it('should call subscribe service when topic is NOT liked', () => {
    const unlikedTopic = mockTopics[0]; // Java (liked: false)
    
    component.handleSubscription(unlikedTopic);

    // On vérifie que la méthode subscribe du service est appelée
    expect(mockTopicService.subscribe).toHaveBeenCalledWith(unlikedTopic.id);
  });

  it('should NOT call subscribe service when topic IS ALREADY liked', () => {
    // On réinitialise l'espion pour ce test
    mockTopicService.subscribe.calls.reset();
    
    const likedTopic = mockTopics[1]; // Angular (liked: true)
    
    component.handleSubscription(likedTopic);

    // On vérifie que la méthode n'est JAMAIS appelée à cause du 'if (!topic.liked)'
    expect(mockTopicService.subscribe).not.toHaveBeenCalled();
  });
});
