import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TriggerEventsListComponent } from './trigger-events-list.component';

describe('TriggerEventsListComponent', () => {
  let component: TriggerEventsListComponent;
  let fixture: ComponentFixture<TriggerEventsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TriggerEventsListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TriggerEventsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
