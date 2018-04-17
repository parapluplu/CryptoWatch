import { TestBed, inject } from '@angular/core/testing';

import { TriggerEventsService } from './triggerevents.service';

describe('TriggereventsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TriggerEventsService]
    });
  });

  it('should be created', inject([TriggerEventsService], (service: TriggerEventsService) => {
    expect(service).toBeTruthy();
  }));
});
