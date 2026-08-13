import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Student, StudentRequest } from '../models/student';

@Injectable({ providedIn: 'root' })
export class StudentService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/students';

  getStudents(search?: string): Observable<Student[]> {
    let params = new HttpParams();
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<Student[]>(this.baseUrl, { params });
  }

  getStudent(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.baseUrl}/${id}`);
  }

  createStudent(request: StudentRequest): Observable<Student> {
    return this.http.post<Student>(this.baseUrl, request);
  }

  updateStudent(id: number, request: StudentRequest): Observable<Student> {
    return this.http.put<Student>(`${this.baseUrl}/${id}`, request);
  }

  deleteStudent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  enrollCourse(studentId: number, courseId: number): Observable<Student> {
    return this.http.post<Student>(`${this.baseUrl}/${studentId}/courses/${courseId}`, null);
  }

  removeCourse(studentId: number, courseId: number): Observable<Student> {
    return this.http.delete<Student>(`${this.baseUrl}/${studentId}/courses/${courseId}`);
  }
}
