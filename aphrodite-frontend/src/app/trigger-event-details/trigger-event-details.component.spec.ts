import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TriggerEventDetailsComponent } from './trigger-event-details.component';

describe('TriggerEventDetailsComponent', () => {
  let component: TriggerEventDetailsComponent;
  let fixture: ComponentFixture<TriggerEventDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TriggerEventDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TriggerEventDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
