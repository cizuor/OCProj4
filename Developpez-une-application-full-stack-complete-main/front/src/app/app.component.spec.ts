import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { of, throwError } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { SessionService } from './core/services/session.service';
import { RouterTestingModule } from '@angular/router/testing';
import { User } from './core/interfaces/user.interface';

describe('AppComponent', () => {

  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let sessionService: SessionService;
  let authService: AuthService;


  const mockAuthService = {
    me: jasmine.createSpy('me')
  };

  const mockSessionService = {
    $isLogged: jasmine.createSpy('$isLogged').and.returnValue(of(false)),
    logIn: jasmine.createSpy('logIn'),
    logOut: jasmine.createSpy('logOut')
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [AppComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    authService = TestBed.inject(AuthService);
    
    // On vide le localStorage avant chaque test
    localStorage.clear();
  });

  

  it('should create the app', () => {
    //const fixture = TestBed.createComponent(AppComponent);
    //const app = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
 it('should call logIn when a valid token exists in localStorage', () => {
    // ARRANGE
    const mockUser: User = { id: 1, email: 'test@test.fr', pseudo: 'test', createdAt: '', updatedAt: '' };
    localStorage.setItem('token', 'fake-jwt-token');
    mockAuthService.me.and.returnValue(of(mockUser));

    // ACT
    component.ngOnInit(); // Déclenche autoLogin()

    // ASSERT
    expect(authService.me).toHaveBeenCalled();
    expect(sessionService.logIn).toHaveBeenCalledWith(mockUser);
  });

  it('should call logOut when token exists but me() API returns an error', () => {
    // ARRANGE
    localStorage.setItem('token', 'invalid-token');
    mockAuthService.me.and.returnValue(throwError(() => new Error('Invalid token')));

    // ACT
    component.ngOnInit();

    // ASSERT
    expect(authService.me).toHaveBeenCalled();
    expect(sessionService.logOut).toHaveBeenCalled();
  });

  it('should not call me() if no token is found in localStorage', () => {
    // ARRANGE
    localStorage.removeItem('token');

    // ACT
    component.ngOnInit();

    // ASSERT
    expect(authService.me).not.toHaveBeenCalled();
  });

  it('should call sessionService.logOut when logout() is triggered', () => {
    // ACT
    component.logout();

    // ASSERT
    expect(sessionService.logOut).toHaveBeenCalled();
  });

});
