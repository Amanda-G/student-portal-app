export interface CourseSummary {
  id: number;
  courseCode: string;
  courseName: string;
}

export interface Student {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  dateOfBirth: string;
  courses: CourseSummary[];
}

export interface StudentRequest {
  firstName: string;
  lastName: string;
  email: string;
  dateOfBirth: string;
}
