import { TestBed, inject } from '@angular/core/testing';

import { EnrichedTransferMessageService } from './enriched-transfer-message.service';

describe('EnrichedTransferMessageService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [EnrichedTransferMessageService]
    });
  });

  it('should be created', inject([EnrichedTransferMessageService], (service: EnrichedTransferMessageService) => {
    expect(service).toBeTruthy();
  }));
});
