import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TriggereventsListComponent } from './triggerevents-list.component';

describe('TriggereventsListComponent', () => {
  let component: TriggereventsListComponent;
  let fixture: ComponentFixture<TriggereventsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TriggereventsListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TriggereventsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
