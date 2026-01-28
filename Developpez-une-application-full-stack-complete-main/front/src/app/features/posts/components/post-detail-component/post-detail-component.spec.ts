import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { PostDetailComponent } from './post-detail-component';
import { PostService } from 'src/app/core/services/post-service';
import { CommentService } from 'src/app/core/services/comment-service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Post } from 'src/app/core/interfaces/post.interface';
import { PostComment } from 'src/app/core/interfaces/comment.interface';
import { of } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;
  let postService: any;
  let commentService: any;


  const mockPost: Post = { 
    id: 1, 
    titre: 'Titre de test', 
    contenu: 'Contenu de test', 
    topicId: 1, 
    topicName: 'Java', 
    userName: 'Admin', 
    userID: 1,
    createdAt: ''  
  };
  const mockComments: PostComment[] = [
    { id: 1, contenu: 'Super !', authorName: 'User1', createdAt: '' }
  ];

  const expectJasmine = (expect as any);

  beforeEach(async () => {

    const postSpy = jasmine.createSpyObj('PostService', ['getById']);
    const commentSpy = jasmine.createSpyObj('CommentService', ['getAllCommentFromPost', 'create']);

     // Configuration des retours par défaut
    postSpy.getById.and.returnValue(of(mockPost));
    commentSpy.getAllCommentFromPost.and.returnValue(of(mockComments));
    commentSpy.create.and.returnValue(of({ id: 2, contenu: 'Nouveau', authorName: 'Moi' }));


    await TestBed.configureTestingModule({
      imports: [PostDetailComponent,
        ReactiveFormsModule, 
        RouterTestingModule
      ],
        providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      provideRouter([]), 
        { provide: PostService, useValue: postSpy },
        { provide: CommentService, useValue: commentSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: { get: (key: string) => '1' } }
          }
        }
    ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService);
    commentService = TestBed.inject(CommentService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expectJasmine(component).toBeTruthy();
  });

  it('should load post and comments on init', () => {

    expectJasmine(component.postId).toBe(1);
    expectJasmine(postService.getById).toHaveBeenCalledWith(1);
    expectJasmine(commentService.getAllCommentFromPost).toHaveBeenCalledWith(1);
    
    expectJasmine(component.post).toEqual(mockPost);
    expectJasmine(component.comments).toHaveSize(1);
  });

  it('should add a comment and reset form on success', () => {
    
    // 1. Remplir le formulaire
    component.commentForm.controls['contenu'].setValue('Mon nouveau commentaire');

    // 2. Appeler la méthode
    component.sendComment();

    // 3. ASSERTIONS
    // Vérifie l'appel au service
    expectJasmine(commentService.create).toHaveBeenCalled();
    
    // Vérifie que la liste locale a été mise à jour (1 ancien + 1 nouveau)
    expectJasmine(component.comments).toHaveSize(2);
    expectJasmine(component.comments[1].contenu).toBe('Nouveau');

    // Vérifie que le formulaire a été vidé
    expectJasmine(component.commentForm.get('contenu')?.value).toBeNull();
  });

  it('should have an invalid form if comment is empty', () => {
    component.commentForm.controls['contenu'].setValue('');
    expectJasmine(component.commentForm.valid).toBeFalse();
  });
});
