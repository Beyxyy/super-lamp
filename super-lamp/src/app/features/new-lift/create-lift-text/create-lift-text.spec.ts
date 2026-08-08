import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateLiftText } from './create-lift-text';

describe('CreateLiftText', () => {
  let component: CreateLiftText;
  let fixture: ComponentFixture<CreateLiftText>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateLiftText]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateLiftText);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
