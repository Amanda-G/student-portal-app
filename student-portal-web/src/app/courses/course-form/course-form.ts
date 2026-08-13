import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CourseService } from '../../services/course.service';

@Component({
  selector: 'app-course-form',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './course-form.html',
  styleUrl: './course-form.scss',
})
export class CourseForm implements OnInit {
  private fb = inject(FormBuilder);
  private courseService = inject(CourseService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  courseId: number | null = null;
  saving = signal(false);
  loading = signal(false);

  form = this.fb.nonNullable.group({
    courseCode: ['', Validators.required],
    courseName: ['', Validators.required],
    description: [''],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      return;
    }
    this.courseId = Number(idParam);
    this.loading.set(true);
    this.courseService.getCourse(this.courseId).subscribe({
      next: (course) => {
        this.form.patchValue(course);
        this.loading.set(false);
      },
      error: () => {
        this.snackBar.open('Could not load course', 'Close', { duration: 4000 });
        this.router.navigate(['/courses']);
      },
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    const request = this.form.getRawValue();
    const call = this.courseId
      ? this.courseService.updateCourse(this.courseId, request)
      : this.courseService.createCourse(request);

    call.subscribe({
      next: () => {
        this.snackBar.open(this.courseId ? 'Course updated' : 'Course created', 'Close', { duration: 3000 });
        this.router.navigate(['/courses']);
      },
      error: (err) => {
        this.saving.set(false);
        const message = err?.error?.message ?? 'Could not save course';
        this.snackBar.open(message, 'Close', { duration: 4000 });
      },
    });
  }

  errorFor(field: string): string | null {
    const control = this.form.get(field);
    if (!control || !control.touched || !control.errors) {
      return null;
    }
    if (control.errors['required']) {
      return 'This field is required';
    }
    return null;
  }
}
