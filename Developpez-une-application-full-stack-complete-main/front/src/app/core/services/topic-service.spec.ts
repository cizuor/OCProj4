import { TestBed } from '@angular/core/testing';

import { TopicService } from './topic-service';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('TopicService', () => {
  let service: TopicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(TopicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
