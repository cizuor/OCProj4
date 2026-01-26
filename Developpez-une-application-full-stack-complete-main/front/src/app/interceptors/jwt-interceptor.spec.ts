import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpInterceptorFn, provideHttpClient, withInterceptors } from '@angular/common/http';

import { jwtInterceptor } from './jwt-interceptor';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

describe('jwtInterceptor', () => {

  let httpClient: HttpClient;
  let httpMock: HttpTestingController;


  const interceptor: HttpInterceptorFn = (req, next) => 
    TestBed.runInInjectionContext(() => jwtInterceptor(req, next));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([jwtInterceptor])),
        provideHttpClientTesting(),
      ],});

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

   afterEach(() => {
    // Vérifie qu'il n'y a pas de requêtes HTTP non gérées
    httpMock.verify();
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });


  it('should add Authorization header when token is present in localStorage', () => {
    // 1. ARRANGE : On place un faux token dans le storage
    const mockToken = 'fake-token-123';
    localStorage.setItem('token', mockToken);

    // 2. ACT : On lance une requête bidon
    httpClient.get('/api/test').subscribe();

    // 3. ASSERT : On attrape la requête sortante
    const req = httpMock.expectOne('/api/test');
    
    // On vérifie que le header est présent et correct
    expect(req.request.headers.has('Authorization')).toBeTrue();
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
  });

  it('should NOT add Authorization header when token is absent', () => {
    // 1. ARRANGE : On s'assure que le storage est vide
    localStorage.removeItem('token');

    // 2. ACT : On lance une requête bidon
    httpClient.get('/api/test').subscribe();

    // 3. ASSERT
    const req = httpMock.expectOne('/api/test');
    
    // On vérifie que le header n'existe pas
    expect(req.request.headers.has('Authorization')).toBeFalse();
  });
});
