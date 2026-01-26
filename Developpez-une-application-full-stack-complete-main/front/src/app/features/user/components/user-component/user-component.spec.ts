/// <reference types="jasmine" />

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserComponent } from './user-component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TopicCardComponent } from 'src/app/features/topic/components/topic-card-component/topic-card-component';
import { UserService } from 'src/app/core/services/user-service';
import { TopicService } from 'src/app/core/services/topic-service';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { Topic } from 'src/app/core/interfaces/topic.interface';

describe('UserComponent', () => {
  let component: UserComponent;
  let fixture: ComponentFixture<UserComponent>;


  const mockUserService = {
    getMe: jasmine.createSpy('getMe').and.returnValue(of({ id: 1, pseudo: 'testUser', email: 'test@test.fr' })),
    update: jasmine.createSpy('update').and.returnValue(of({ id: 1, pseudo: 'newPseudo', email: 'new@test.fr' }))
  };

  const mockTopicService = {
    getFolowed: jasmine.createSpy('getFolowed').and.returnValue(of([
      { id: 1, title: 'Java', description: 'Desc', liked: true }
    ])),
    unsubscribe: jasmine.createSpy('unsubscribe').and.returnValue(of([])),
    subscribe: jasmine.createSpy('subscribe').and.returnValue(of([]))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserComponent,
        ReactiveFormsModule,
        HttpClientTestingModule,
        TopicCardComponent
      ],
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: mockUserService },
        { provide: TopicService, useValue: mockTopicService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with user data', () => {
    expect(component.userForm.value.pseudo).toEqual('testUser');
    expect(component.userForm.value.email).toEqual('test@test.fr');
  });

  it('should call update service when form is submitted', () => {
    component.userForm.controls['pseudo'].setValue('newPseudo');
    
    component.onSave();
    expect(mockUserService.update).toHaveBeenCalled();
  });

  it('should be invalid if email is incorrect', () => {
    component.userForm.controls['email'].setValue('mauvais-email');
    expect(component.userForm.valid).toBeFalse();
  });

  it('should call unsubscribe and reload list when a topic is untoggled', () => {
    const mockTopic = { id: 1, title: 'Java', description: 'Desc', liked: true };
    
    component.handleSubscription(mockTopic);

    expect(mockTopicService.unsubscribe).toHaveBeenCalledWith(1);
    expect(mockTopicService.getFolowed).toHaveBeenCalled();
  });


  it('should include password in update if provided', () => {
    // ARRANGE
    component.userForm.patchValue({
      pseudo: 'test',
      email: 'test@test.fr',
      password: 'newPassword123'
    });

    // ACT
    component.onSave();

    // ASSERT
    // On vérifie que l'objet envoyé au service contient bien le mot de passe
    expect(mockUserService.update).toHaveBeenCalledWith(jasmine.objectContaining({
      password: 'newPassword123'
    }));
  });

  it('should filter out unliked topics when unsubscribing', () => {
    // ARRANGE
    const mockTopicsFromServer = [
      { id: 1, title: 'Java', liked: false },
      { id: 2, title: 'Angular', liked: true }
    ];
    // On change le retour du spy pour ce test précis
    mockTopicService.unsubscribe.and.returnValue(of(mockTopicsFromServer));
    
    // ACT
    component.handleSubscription({ id: 1 } as Topic);

    // ASSERT
    // La liste ne doit contenir que le sujet dont liked est true (Angular)
    expect(component.lTopics.length).toBe(1);
    expect(component.lTopics[0].title).toBe('Angular');
  });

  it('should log error when update fails', () => {
    // ARRANGE
    const consoleSpy = spyOn(console, 'error');
    mockUserService.update.and.returnValue(throwError(() => new Error('Server Error')));

    // ACT
    component.onSave();

    // ASSERT
    expect(consoleSpy).toHaveBeenCalled();
  });
});
