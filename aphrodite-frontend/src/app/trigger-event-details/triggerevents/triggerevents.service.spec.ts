import { TestBed, inject } from '@angular/core/testing';

import { TriggereventsService } from './triggerevents.service';

describe('TriggereventsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TriggereventsService]
    });
  });

  it('should be created', inject([TriggereventsService], (service: TriggereventsService) => {
    expect(service).toBeTruthy();
  }));
});
