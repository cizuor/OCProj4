import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login-component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from 'src/app/core/services/auth.service';
import { SessionService } from 'src/app/core/services/session.service';
import { of, throwError } from 'rxjs';
import { User } from 'src/app/core/interfaces/user.interface';
import { JwtResponse } from 'src/app/core/interfaces/jwtResponse.interface';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: any;
  let sessionService: any;
  let router: Router;


   const expectJasmine = (expect as any);

  beforeEach(async () => {

    const authSpy = jasmine.createSpyObj('AuthService', ['login', 'me']);
    const sessionSpy = jasmine.createSpyObj('SessionService', ['logIn']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent,
        ReactiveFormsModule, 
        RouterTestingModule
      ],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
         { provide: AuthService, useValue: authSpy },
        { provide: SessionService, useValue: sessionSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;

    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);

    // Espion sur la navigation et le localStorage
    spyOn(router, 'navigate');
    spyOn(localStorage, 'setItem');

    fixture.detectChanges();
  });

  it('should create', () => {
    expectJasmine(component).toBeTruthy();
  });

  it('should have an invalid form when empty', () => {
    expectJasmine(component.form.valid).toBeFalse();
  });

  it('should have a valid form when fields are filled correctly', () => {
    component.form.patchValue({
      login: 'test@test.fr',
      password: 'password123'
    });
    expectJasmine(component.form.valid).toBeTrue();
  });

  it('should call login, store token, load user and navigate on success', () => {
    // ARRANGE
    const mockJwt: JwtResponse = { token: 'fake-token', type: 'Bearer', id: 1, username: 'test' };
    const mockUser: User = { id: 1, email: 'test@test.fr', pseudo: 'test', createdAt: '', updatedAt: '' };
    
    authService.login.and.returnValue(of(mockJwt));
    authService.me.and.returnValue(of(mockUser));

    component.form.patchValue({
      login: 'test@test.fr',
      password: 'password123'
    });

    // ACT
    component.submit();

    // ASSERT
    expectJasmine(authService.login).toHaveBeenCalled();
    expectJasmine(localStorage.setItem).toHaveBeenCalledWith('token', 'fake-token');
    expectJasmine(authService.me).toHaveBeenCalled();
    expectJasmine(sessionService.logIn).toHaveBeenCalledWith(mockUser);
    expectJasmine(router.navigate).toHaveBeenCalledWith(['/articles']);
    expectJasmine(component.error).toBeFalse();
  });

  it('should set error to true when login fails', () => {
    // ARRANGE
    authService.login.and.returnValue(throwError(() => new Error('Invalid credentials')));
    
    component.form.patchValue({
      login: 'wrong',
      password: 'wrongpassword'
    });

    // ACT
    component.submit();

    // ASSERT
    expectJasmine(component.error).toBeTrue();
    expectJasmine(router.navigate).not.toHaveBeenCalled();
  });
});
