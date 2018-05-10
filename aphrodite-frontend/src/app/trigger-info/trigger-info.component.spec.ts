import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TriggerInfoComponent } from './trigger-info.component';

describe('TriggerInfoComponent', () => {
  let component: TriggerInfoComponent;
  let fixture: ComponentFixture<TriggerInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TriggerInfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TriggerInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
