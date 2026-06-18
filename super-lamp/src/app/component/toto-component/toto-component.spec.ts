import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TotoComponent } from './toto-component';

describe('TotoComponent', () => {
  let component: TotoComponent;
  let fixture: ComponentFixture<TotoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TotoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
