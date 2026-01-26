import { TestBed } from '@angular/core/testing';

import { CommentService } from './comment-service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('CommentService', () => {
  let service: CommentService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [
      CommentService,
      provideHttpClient(), // <--- AJOUTE CECI
      provideHttpClientTesting() // <--- AJOUTE CECI pour intercepter les appels
    ]});
    service = TestBed.inject(CommentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
