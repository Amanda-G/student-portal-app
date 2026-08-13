import { Component, OnInit, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { StudentService } from '../../services/student.service';

function notInFuture(control: AbstractControl): ValidationErrors | null {
  const today = new Date().toISOString().split('T')[0];
  if (control.value && control.value > today) {
    return { future: true };
  }
  return null;
}

@Component({
  selector: 'app-student-form',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './student-form.html',
  styleUrl: './student-form.scss',
})
export class StudentForm implements OnInit {
  private fb = inject(FormBuilder);
  private studentService = inject(StudentService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  studentId: number | null = null;
  saving = signal(false);
  loading = signal(false);

  form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    dateOfBirth: ['', [Validators.required, notInFuture]],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      return;
    }
    this.studentId = Number(idParam);
    this.loading.set(true);
    this.studentService.getStudent(this.studentId).subscribe({
      next: (student) => {
        this.form.patchValue(student);
        this.loading.set(false);
      },
      error: () => {
        this.snackBar.open('Could not load student', 'Close', { duration: 4000 });
        this.router.navigate(['/students']);
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
    const call = this.studentId
      ? this.studentService.updateStudent(this.studentId, request)
      : this.studentService.createStudent(request);

    call.subscribe({
      next: () => {
        this.snackBar.open(this.studentId ? 'Student updated' : 'Student created', 'Close', { duration: 3000 });
        this.router.navigate(['/students']);
      },
      error: (err) => {
        this.saving.set(false);
          const message = err?.error?.message ?? 'Could not save student';
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
    if (control.errors['email']) {
      return 'Enter a valid email';
    }
    if (control.errors['future']) {
      return 'Date of birth cannot be in the future';
    }
    if (control.errors['server']) {
      return control.errors['server'];
    }
    return null;
  }
}
