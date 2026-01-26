import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { PostDetailComponent } from './post-detail-component';
import { PostService } from 'src/app/core/services/post-service';
import { CommentService } from 'src/app/core/services/comment-service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostDetailComponent],
        providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      provideRouter([]), 
    ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
