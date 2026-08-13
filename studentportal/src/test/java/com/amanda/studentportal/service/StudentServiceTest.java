package com.amanda.studentportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amanda.studentportal.dto.StudentRequest;
import com.amanda.studentportal.dto.StudentResponse;
import com.amanda.studentportal.entity.Course;
import com.amanda.studentportal.entity.Student;
import com.amanda.studentportal.exception.ConflictException;
import com.amanda.studentportal.exception.NotFoundException;
import com.amanda.studentportal.repository.CourseRepository;
import com.amanda.studentportal.repository.StudentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private CourseRepository courseRepository;

  @InjectMocks
  private StudentService studentService;

  @Test
  void getStudentsReturnsAllWhenNoSearch() {
    // Arrange
    when(studentRepository.findAll()).thenReturn(List.of(buildStudent(1L), buildStudent(2L)));

    // Act
    List<StudentResponse> result = studentService.getStudents(null);

    // Assert
    assertEquals(2, result.size());
  }

  @Test
  void getStudentsUsesSearchWhenGiven() {
    // Arrange
    when(studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("john", "john"))
        .thenReturn(List.of(buildStudent(1L)));

    // Act
    List<StudentResponse> result = studentService.getStudents("john");

    // Assert
    assertEquals(1, result.size());
    verify(studentRepository, never()).findAll();
  }

  @Test
  void getStudentReturnsStudent() {
    // Arrange
    when(studentRepository.findById(1L)).thenReturn(Optional.of(buildStudent(1L)));

    // Act
    StudentResponse result = studentService.getStudent(1L);

    // Assert
    assertEquals("John", result.firstName());
    assertEquals("Smith", result.lastName());
  }

  @Test
  void getStudentThrowsWhenNotFound() {
    // Arrange
    when(studentRepository.findById(99L)).thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(NotFoundException.class, () -> studentService.getStudent(99L));
  }

  @Test
  void createStudentSavesAndReturnsStudent() {
    // Arrange
    StudentRequest request = new StudentRequest("Sara", "Kim", "sara@gmail.com", LocalDate.of(2003, 5, 15));
    when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

    // Act
    StudentResponse result = studentService.createStudent(request);

    // Assert
    assertEquals("Sara", result.firstName());
    assertEquals("sara@gmail.com", result.email());
    verify(studentRepository).save(any(Student.class));
  }

  @Test
  void updateStudentChangesFields() {
    // Arrange
    Student student = buildStudent(1L);
    StudentRequest request = new StudentRequest("Johnny", "Smith", "johnny@gmail.com", LocalDate.of(2001, 4, 12));
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

    // Act
    StudentResponse result = studentService.updateStudent(1L, request);

    // Assert
    assertEquals("Johnny", result.firstName());
    assertEquals("johnny@gmail.com", result.email());
  }

  @Test
  void deleteStudentDeletes() {
    // Arrange
    Student student = buildStudent(1L);
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

    // Act
    studentService.deleteStudent(1L);

    // Assert
    verify(studentRepository).delete(student);
  }

  @Test
  void enrollCourseAddsCourse() {
    // Arrange
    Student student = buildStudent(1L);
    Course course = buildCourse(1L);
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

    // Act
    StudentResponse result = studentService.enrollCourse(1L, 1L);

    // Assert
    assertEquals(1, result.courses().size());
    assertTrue(student.getCourses().contains(course));
  }

  @Test
  void enrollCourseThrowsWhenAlreadyEnrolled() {
    // Arrange
    Student student = buildStudent(1L);
    Course course = buildCourse(1L);
    student.getCourses().add(course);
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

    // Act + Assert
    assertThrows(ConflictException.class, () -> studentService.enrollCourse(1L, 1L));
    verify(studentRepository, never()).save(any());
  }

  @Test
  void enrollCourseThrowsWhenCourseMissing() {
    // Arrange
    Student student = buildStudent(1L);
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(courseRepository.findById(99L)).thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(NotFoundException.class, () -> studentService.enrollCourse(1L, 99L));
  }

  @Test
  void removeCourseRemovesCourse() {
    // Arrange
    Student student = buildStudent(1L);
    Course course = buildCourse(1L);
    student.getCourses().add(course);
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

    // Act
    StudentResponse result = studentService.removeCourse(1L, 1L);

    // Assert
    assertEquals(0, result.courses().size());
  }

  @Test
  void removeCourseThrowsWhenNotEnrolled() {
    // Arrange
    Student student = buildStudent(1L);
    Course course = buildCourse(1L);
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

    // Act + Assert
    assertThrows(NotFoundException.class, () -> studentService.removeCourse(1L, 1L));
  }

  private Student buildStudent(Long id) {
    Student student = new Student();
    student.setId(id);
    student.setFirstName("John");
    student.setLastName("Smith");
    student.setEmail("john.smith@gmail.com");
    student.setDateOfBirth(LocalDate.of(2001, 4, 12));
    return student;
  }

  private Course buildCourse(Long id) {
    Course course = new Course();
    course.setId(id);
    course.setCourseCode("CS101");
    course.setCourseName("Intro to Programming");
    course.setDescription("Basics of programming with Java");
    return course;
  }
}
