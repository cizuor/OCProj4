import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostComponent } from './post-component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { TopicService } from 'src/app/core/services/topic-service';
import { PostService } from 'src/app/core/services/post-service';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('PostComponent', () => {
  let component: PostComponent;
  let fixture: ComponentFixture<PostComponent>;
  let router: Router;

  let topicService: any;
  let postService: any;

  
  const expectJasmine = (expect as any);

  beforeEach(async () => {

    const topicSpy = jasmine.createSpyObj('TopicService', ['getAll']);
    const postSpy = jasmine.createSpyObj('PostService', ['create']);

    topicSpy.getAll.and.returnValue(of([
      { id: 1, title: 'Java', description: 'Desc', liked: false },
      { id: 2, title: 'Angular', description: 'Desc', liked: false }
    ]));
    postSpy.create.and.returnValue(of({}));

    
    await TestBed.configureTestingModule({
      imports: [PostComponent,
        ReactiveFormsModule, 
        CommonModule],
       providers: [
        provideRouter([]),
        { provide: PostService, useValue: postSpy },
        { provide: TopicService, useValue: topicSpy },
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    topicService = TestBed.inject(TopicService); // On récupère l'instance du mock
    postService = TestBed.inject(PostService);

    spyOn(router, 'navigate');

  });



  it('should create', () => {
    fixture.detectChanges();
    expectJasmine(component).toBeTruthy();
  });


  it('should load topics on initialization', () => {
    fixture.detectChanges(); // ngOnInit s'exécute ici avec le mock propre
    expectJasmine(component.topics.length).toBe(2);
    expectJasmine(component.topics[0].title).toBe('Java');
  });

  it('should log error when topic loading fails', () => {
    const consoleSpy = spyOn(console, 'error');
    // On change le mock AVANT le detectChanges
    topicService.getAll.and.returnValue(throwError(() => new Error('API Error')));
    
    fixture.detectChanges(); // ngOnInit s'exécute et tombe sur l'erreur
    
    expectJasmine(consoleSpy).toHaveBeenCalled();
  });

  it('should have an invalid form when empty', () => {
    fixture.detectChanges();
    expectJasmine(component.postForm.valid).toBeFalse();
  });

  it('should be valid when all fields are correctly filled', () => {
    fixture.detectChanges();
    component.postForm.patchValue({
      topicId: '1',
      titre: 'Mon super titre',
      contenu: 'Ceci est un contenu de plus de 10 caractères'
    });
    expectJasmine(component.postForm.valid).toBeTrue();
  });

  it('should not call postService.create if form is invalid', () => {
    // ARRANGE : Formulaire incomplet
    component.postForm.patchValue({ titre: 'Petit titre' });

    // ACT
    component.onSubmit();

    // ASSERT
    expectJasmine(postService.create).not.toHaveBeenCalled();
  });


  it('should navigate to articles on successful post creation', () => {
    // 1. Initialisation
    fixture.detectChanges();
    
    // 2. Remplir le formulaire pour qu'il soit valide
    const validData = {
      topicId: '1',
      titre: 'Mon super titre',
      contenu: 'Un contenu suffisamment long'
    };
    component.postForm.patchValue(validData);

    // 3. Simuler le clic sur envoyer
    component.onSubmit();

    // 4. Vérifier que le service de création a été appelé
    expectJasmine(postService.create).toHaveBeenCalled();
    
    // 5. VÉRIFIER LA REDIRECTION (C'est cette ligne qui couvre le bloc 'next')
    expectJasmine(router.navigate).toHaveBeenCalledWith(['/articles']);
  });

  it('should log error when post creation fails', () => {
    // 1. Préparer l'espion pour intercepter l'erreur console
    const consoleSpy = spyOn(console, 'error');
    
    // 2. Faire en sorte que le service renvoie une erreur
    postService.create.and.returnValue(throwError(() => new Error('Database Error')));
    
    fixture.detectChanges();
    
    // 3. Remplir le formulaire pour pouvoir l'envoyer
    component.postForm.patchValue({
      topicId: '1',
      titre: 'Titre',
      contenu: 'Contenu valide'
    });

    // 4. Lancer la soumission
    component.onSubmit();

    // 5. ASSERT : Vérifier que le bloc 'error' a bien été exécuté
    expectJasmine(consoleSpy).toHaveBeenCalledWith('Erreur lors de la création', jasmine.any(Error));
  });


});
