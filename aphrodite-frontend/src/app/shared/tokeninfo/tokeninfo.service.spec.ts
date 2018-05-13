import { TestBed, inject } from '@angular/core/testing';

import { TokeninfoService } from './tokeninfo.service';

describe('TokeninfoService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TokeninfoService]
    });
  });

  it('should be created', inject([TokeninfoService], (service: TokeninfoService) => {
    expect(service).toBeTruthy();
  }));
});
