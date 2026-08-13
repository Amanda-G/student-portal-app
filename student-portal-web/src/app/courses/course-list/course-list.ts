import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { BehaviorSubject, combineLatest, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, startWith, switchMap } from 'rxjs/operators';
import { Course } from '../../models/course';
import { CourseService } from '../../services/course.service';
import { ConfirmDialog } from '../../common/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-course-list',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatTableModule,
  ],
  templateUrl: './course-list.html',
  styleUrl: './course-list.scss',
})
export class CourseList implements OnInit {
  private courseService = inject(CourseService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private destroyRef = inject(DestroyRef);

  search = new FormControl('', { nonNullable: true });
  courses = signal<Course[]>([]);
  loading = signal(true);

  displayedColumns = ['courseCode', 'courseName', 'description', 'students', 'actions'];

  private refresh$ = new BehaviorSubject<void>(undefined);

  ngOnInit(): void {
    const searchTerm$ = this.search.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
    );

    combineLatest([searchTerm$, this.refresh$])
      .pipe(
        switchMap(([term]) => {
          this.loading.set(true);
          return this.courseService.getCourses(term).pipe(
            catchError(() => {
              this.snackBar.open('Could not load courses', 'Close', { duration: 4000 });
              return of([]);
            }),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((courses) => {
        this.courses.set(courses);
        this.loading.set(false);
      });
  }

  deleteCourse(course: Course): void {
    const ref = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Delete Course',
        message: `Delete ${course.courseCode} - ${course.courseName}?`,
      },
    });

    ref.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.courseService.deleteCourse(course.id).subscribe({
        next: () => {
          this.snackBar.open('Course deleted', 'Close', { duration: 3000 });
          this.refresh$.next();
        },
        error: (err) => {
          const message = err?.error?.message ?? 'Could not delete course';
          this.snackBar.open(message, 'Close', { duration: 4000 });
        },
      });
    });
  }
}
