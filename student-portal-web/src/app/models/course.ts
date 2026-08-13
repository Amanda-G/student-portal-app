export interface StudentSummary {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

export interface Course {
  id: number;
  courseCode: string;
  courseName: string;
  description: string;
  students: StudentSummary[];
}

export interface CourseRequest {
  courseCode: string;
  courseName: string;
  description: string;
}
