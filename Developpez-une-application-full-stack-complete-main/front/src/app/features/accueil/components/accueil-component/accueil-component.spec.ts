import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccueilComponent } from './accueil-component';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionService } from 'src/app/core/services/session.service';
import { By } from '@angular/platform-browser';


describe('AccueilComponent', () => {
  let component: AccueilComponent;
  let fixture: ComponentFixture<AccueilComponent>;
  let router: Router;

  // On crée un BehaviorSubject pour contrôler l'état de connexion pendant le test
  const isLoggedSubject = new BehaviorSubject<boolean>(false);

  // Mock du SessionService
  const mockSessionService = {
    $isLogged: () => isLoggedSubject.asObservable()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccueilComponent,RouterTestingModule],
       providers: [
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AccueilComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });


it('should display Login and Register buttons when user is not logged in', () => {
    // 1. On s'assure que l'état est déconnecté
    isLoggedSubject.next(false);
    
    // 2. On demande à Angular de rafraîchir la vue
    fixture.detectChanges();

    // 3. On vérifie qu'il y a deux boutons
    const buttons = fixture.debugElement.queryAll(By.css('button'));
    expect(buttons.length).toBe(2);
    expect(buttons[0].nativeElement.textContent).toContain('Se connecter');
    expect(buttons[1].nativeElement.textContent).toContain('S\'inscrire');
  });

  it('should redirect to /articles if user becomes logged in', () => {
    // 1. On passe l'état à "connecté"
    isLoggedSubject.next(true);

    // 2. On déclenche ngOnInit (via detectChanges)
    fixture.detectChanges();

    // 3. On vérifie que la redirection a été appelée avec le bon chemin
    expect(router.navigate).toHaveBeenCalledWith(['/articles']);
  });

  it('should have correct routerLinks on buttons', () => {
    fixture.detectChanges();
    
    // On cherche les boutons par leur attribut routerLink
    const loginBtn = fixture.debugElement.query(By.css('button[routerLink="/login"]'));
    const registerBtn = fixture.debugElement.query(By.css('button[routerLink="/register"]'));

    expect(loginBtn).not.toBeNull();
    expect(registerBtn).not.toBeNull();
  });
});
