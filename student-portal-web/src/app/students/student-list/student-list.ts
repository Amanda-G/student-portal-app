import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DatePipe } from '@angular/common';
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
import { Student } from '../../models/student';
import { StudentService } from '../../services/student.service';
import { ConfirmDialog } from '../../common/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-student-list',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatTableModule,
  ],
  templateUrl: './student-list.html',
  styleUrl: './student-list.scss',
})
export class StudentList implements OnInit {

  // Using inject instead of constructor based injection .

  private studentService = inject(StudentService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private destroyRef = inject(DestroyRef);

  search = new FormControl('', { nonNullable: true });

  // using signal instead of BehaviorSubject
  students = signal<Student[]>([]);
  loading = signal(true);

  displayedColumns = ['name', 'email', 'dateOfBirth', 'courses', 'actions'];

  private refresh$ = new BehaviorSubject<void>(undefined);

  ngOnInit(): void {
    const searchTerm$ = this.search.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
    );

    combineLatest([searchTerm$, this.refresh$])
      .pipe(
        switchMap(([term, x]) => {
          this.loading.set(true);
          return this.studentService.getStudents(term)
          .pipe(
            catchError(() => {
              this.snackBar.open('Could not load students', 'Close', { duration: 4000 });
              return of([]);
            }),
          );
        }),
        takeUntilDestroyed(this.destroyRef), // instead of using ngOnDestroy
      )
      .subscribe((students) => {
        this.students.set(students);
        this.loading.set(false);
      });
  }

  deleteStudent(student: Student): void {
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
          this.snackBar.open('Student deleted', 'Close', { duration: 3000 });
          this.refresh$.next();
        },
        error: () => {
          this.snackBar.open('Could not delete student', 'Close', { duration: 4000 });
        },
      });
    });
  }
}
