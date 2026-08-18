import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatDialog} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {CourseSummary, Student} from '../../models/student';
import {Course} from '../../models/course';
import {StudentService} from '../../services/student.service';
import {CourseService} from '../../services/course.service';
import {ConfirmDialog} from '../../common/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-student-detail',
  imports: [
    DatePipe,
    FormsModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatTableModule,
    MatTabsModule,
  ],
  templateUrl: './student-detail.html',
  styleUrl: './student-detail.scss',
})
export class StudentDetail implements OnInit {
  student = signal<Student | null>(null);
  allCourses = signal<Course[]>([]);
  loading = signal(true);
  enrolling = signal(false);
  selectedCourseId: number | null = null;
  displayedColumns = ['courseCode', 'courseName', 'actions'];
  availableCourses = computed(() => {
    const student = this.student();
    if (!student) {
      return [];
    }
    const enrolledIds = new Set(student.courses.map((c) => c.id));
    return this.allCourses().filter((c) => !enrolledIds.has(c.id));
  });
  private studentService = inject(StudentService);
  private courseService = inject(CourseService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.studentService.getStudent(id).subscribe({
      next: (student) => {
        this.student.set(student);
        this.loading.set(false);
      },
      error: () => {
        this.snackBar.open('Could not load student', 'Close', {duration: 4000});
        this.router.navigate(['/students']);
      },
    });
    this.courseService.getCourses().subscribe((courses) => {
      this.allCourses.set(courses);
    });
  }

  enroll(): void {
    const student = this.student();
    if (!student || !this.selectedCourseId) {
      return;
    }
    this.enrolling.set(true);
    this.studentService.enrollCourse(student.id, this.selectedCourseId).subscribe({
      next: (updated) => {
        this.student.set(updated);
        this.selectedCourseId = null;
        this.enrolling.set(false);
        this.snackBar.open('Enrolled in course', 'Close', {duration: 3000});
      },
      error: (err) => {
        this.enrolling.set(false);
        const message = err?.error?.message ?? 'Could not enroll in course';
        this.snackBar.open(message, 'Close', {duration: 4000});
      },
    });
  }

  removeCourse(course: CourseSummary): void {
    const student = this.student();
    if (!student) {
      return;
    }
    const ref = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Remove Course',
        message: `Remove ${course.courseName} from ${student.firstName} ${student.lastName}?`,
      },
    });
    ref.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.studentService.removeCourse(student.id, course.id).subscribe({
        next: (updated) => {
          this.student.set(updated);
          this.snackBar.open('Course removed', 'Close', {duration: 3000});
        },
        error: (err) => {
          const message = err?.error?.message ?? 'Could not remove course';
          this.snackBar.open(message, 'Close', {duration: 4000});
        },
      });
    });
  }

  deleteStudent(): void {
    const student = this.student();
    if (!student) {
      return;
    }
    const ref = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Delete Student',
        message: `Delete ${student.firstName} ${student.lastName}? This cannot be undone.`,
      },
    });
    ref.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.studentService.deleteStudent(student.id).subscribe({
        next: () => {
          this.snackBar.open('Student deleted', 'Close', {duration: 3000});
          this.router.navigate(['/students']);
        },
        error: () => {
          this.snackBar.open('Could not delete student', 'Close', {duration: 4000});
        },
      });
    });
  }
}
