import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserComponent } from './user-component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TopicCardComponent } from 'src/app/features/topic/components/topic-card-component/topic-card-component';
import { UserService } from 'src/app/core/services/user-service';
import { TopicService } from 'src/app/core/services/topic-service';
import { of } from 'rxjs';

describe('UserComponent', () => {
  let component: UserComponent;
  let fixture: ComponentFixture<UserComponent>;


  const mockUserService = {
    getMe: jasmine.createSpy('getMe').and.returnValue(of({ id: 1, pseudo: 'testUser', email: 'test@test.fr' })),
    update: jasmine.createSpy('update').and.returnValue(of({ id: 1, pseudo: 'newPseudo', email: 'new@test.fr' })),
    unsubscribe: jasmine.createSpy('unsubscribe').and.returnValue(of([]))
  };

  const mockTopicService = {
    getFollowed: jasmine.createSpy('getFollowed').and.returnValue(of([
      { id: 1, title: 'Java', description: 'Desc', liked: true }
    ]))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserComponent,
        ReactiveFormsModule,
        HttpClientTestingModule,
        TopicCardComponent
      ],
      providers: [
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

    expect(mockUserService.unsubscribe).toHaveBeenCalledWith(1);
    expect(mockTopicService.getFollowed).toHaveBeenCalled();
  });
});
