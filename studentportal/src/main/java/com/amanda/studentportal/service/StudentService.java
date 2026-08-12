package com.amanda.studentportal.service;

import com.amanda.studentportal.dto.CourseSummary;
import com.amanda.studentportal.dto.StudentRequest;
import com.amanda.studentportal.dto.StudentResponse;
import com.amanda.studentportal.entity.Student;
import com.amanda.studentportal.exception.NotFoundException;
import com.amanda.studentportal.repository.StudentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;

  public List<StudentResponse> getStudents(String search) {
    List<Student> students;
    if (search == null || search.isBlank()) {
      students = studentRepository.findAll();
    } else {
      students = studentRepository
          .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(search, search);
    }
    return students.stream().map(this::toResponse).toList();
  }

  public StudentResponse getStudent(Long id) {
    Student student = findStudent(id);
    return toResponse(student);
  }

  public StudentResponse createStudent(StudentRequest request) {
    Student student = new Student();
    applyRequest(student, request);
    Student save = studentRepository.save(student);
    return toResponse(save);
  }

  public StudentResponse updateStudent(Long id, StudentRequest request) {
    Student student = findStudent(id);
    applyRequest(student, request);
    studentRepository.save(student); // we can also use transactional annotation instead to call save here.
    return toResponse(student);
  }

  public void deleteStudent(Long id) {
    Student student = findStudent(id);
    studentRepository.delete(student);
  }

  private Student findStudent(Long id) {
    return studentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Student not found with id " + id));
  }

  private void applyRequest(Student student, StudentRequest request) {
    student.setFirstName(request.firstName());
    student.setLastName(request.lastName());
    student.setEmail(request.email());
    student.setDateOfBirth(request.dateOfBirth());
  }

  private StudentResponse toResponse(Student student) {
    List<CourseSummary> courses = student.getCourses().stream()
        .map(c -> new CourseSummary(
            c.getId(),
            c.getCourseCode(),
            c.getCourseName())
        )
        .toList();
    return new StudentResponse(
        student.getId(),
        student.getFirstName(),
        student.getLastName(),
        student.getEmail(),
        student.getDateOfBirth(),
        courses);
  }
}
