import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { StudentDetail } from './student-detail';

describe('StudentDetail', () => {
  let component: StudentDetail;
  let fixture: ComponentFixture<StudentDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentDetail],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(StudentDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('filters already enrolled courses out of the dropdown', () => {
    component.student.set({
      id: 1,
      firstName: 'John',
      lastName: 'Smith',
      email: 'john@gmail.com',
      dateOfBirth: '2001-04-12',
      courses: [{ id: 1, courseCode: 'CS101', courseName: 'Intro to Programming' }],
    });
    component.allCourses.set([
      { id: 1, courseCode: 'CS101', courseName: 'Intro to Programming', description: '', students: [] },
      { id: 2, courseCode: 'MATH201', courseName: 'Linear Algebra', description: '', students: [] },
    ]);

    const available = component.availableCourses();

    expect(available.length).toBe(1);
    expect(available[0].courseCode).toBe('MATH201');
  });
});
