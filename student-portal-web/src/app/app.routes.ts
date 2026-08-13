import { Routes } from '@angular/router';
import { Home } from './home/home';
import { StudentList } from './students/student-list/student-list';
import { StudentDetail } from './students/student-detail/student-detail';
import { StudentForm } from './students/student-form/student-form';
import { CourseList } from './courses/course-list/course-list';
import { CourseDetail } from './courses/course-detail/course-detail';
import { CourseForm } from './courses/course-form/course-form';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'students', component: StudentList },
  { path: 'students/new', component: StudentForm },
  { path: 'students/:id', component: StudentDetail },
  { path: 'students/:id/edit', component: StudentForm },
  { path: 'courses', component: CourseList },
  { path: 'courses/new', component: CourseForm },
  { path: 'courses/:id', component: CourseDetail },
  { path: 'courses/:id/edit', component: CourseForm },
  { path: '**', redirectTo: '' },
];
