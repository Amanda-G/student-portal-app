import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { Course } from '../../models/course';
import { CourseService } from '../../services/course.service';
import { ConfirmDialog } from '../../common/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-course-detail',
  imports: [
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatTabsModule,
  ],
  templateUrl: './course-detail.html',
  styleUrl: './course-detail.scss',
})
export class CourseDetail implements OnInit {
  private courseService = inject(CourseService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  course = signal<Course | null>(null);
  loading = signal(true);

  displayedColumns = ['name', 'email'];

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.courseService.getCourse(id).subscribe({
      next: (course) => {
        this.course.set(course);
        this.loading.set(false);
      },
      error: () => {
        this.snackBar.open('Could not load course', 'Close', { duration: 4000 });
        this.router.navigate(['/courses']);
      },
    });
  }

  deleteCourse(): void {
    const course = this.course();
    if (!course) {
      return;
    }
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
          this.router.navigate(['/courses']);
        },
        error: (err) => {
          const message = err?.error?.message ?? 'Could not delete course';
          this.snackBar.open(message, 'Close', { duration: 4000 });
        },
      });
    });
  }
}
