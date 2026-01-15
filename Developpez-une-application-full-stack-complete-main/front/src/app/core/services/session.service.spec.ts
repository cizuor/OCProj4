import { TestBed } from '@angular/core/testing';

import { SessionService } from './session.service';
import { User } from 'src/app/core/interfaces/user.interface';

describe('Session', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  it('should start with isLogged as false', (done) => {
    service.$isLogged().subscribe((logged) => {
      expect(logged).toBeFalse();
      done();
    });
  });

  it('should set isLogged to true after logIn', (done) => {
    const mockUser = { id: 1, email: 'test@test.com', pseudo: 'test' } as User;
    
    service.logIn(mockUser);
    
    expect(service.isLogged).toBeTrue();
    expect(service.user).toEqual(mockUser);
    
    service.$isLogged().subscribe((logged) => {
      expect(logged).toBeTrue();
      done();
    });
  });

  it('should set isLogged to false after logOut', (done) => {
    service.logOut();
    
    expect(service.isLogged).toBeFalse();
    expect(service.user).toBeUndefined();
    
    service.$isLogged().subscribe((logged) => {
      expect(logged).toBeFalse();
      done();
    });
  });

});
