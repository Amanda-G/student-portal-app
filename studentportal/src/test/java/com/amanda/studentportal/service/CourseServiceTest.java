package com.amanda.studentportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amanda.studentportal.dto.CourseRequest;
import com.amanda.studentportal.dto.CourseResponse;
import com.amanda.studentportal.entity.Course;
import com.amanda.studentportal.entity.Student;
import com.amanda.studentportal.exception.ConflictException;
import com.amanda.studentportal.exception.NotFoundException;
import com.amanda.studentportal.repository.CourseRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

  @Mock
  private CourseRepository courseRepository;

  @InjectMocks
  private CourseService courseService;

  @Test
  void createCourseSavesAndReturnsCourse() {
    // Arrange
    CourseRequest request = new CourseRequest("HIST110", "World History", "From ancient to modern");
    when(courseRepository.existsByCourseCodeIgnoreCase("HIST110")).thenReturn(false);
    when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

    // Act
    CourseResponse result = courseService.createCourse(request);

    // Assert
    assertEquals("HIST110", result.courseCode());
    assertEquals("World History", result.courseName());
  }

  @Test
  void createCourseThrowsWhenCodeExists() {
    // Arrange
    CourseRequest request = new CourseRequest("CS101", "Another Course", "dup");
    when(courseRepository.existsByCourseCodeIgnoreCase("CS101")).thenReturn(true);

    // Act + Assert
    assertThrows(ConflictException.class, () -> courseService.createCourse(request));
    verify(courseRepository, never()).save(any());
  }

  @Test
  void getCourseThrowsWhenNotFound() {
    // Arrange
    when(courseRepository.findById(99L)).thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(NotFoundException.class, () -> courseService.getCourse(99L));
  }

  @Test
  void updateCourseThrowsWhenCodeTakenByAnother() {
    // Arrange
    Course course = buildCourse(1L);
    CourseRequest request = new CourseRequest("MATH201", "Renamed", "x");
    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
    when(courseRepository.existsByCourseCodeIgnoreCaseAndIdNot("MATH201", 1L)).thenReturn(true);

    // Act + Assert
    assertThrows(ConflictException.class, () -> courseService.updateCourse(1L, request));
  }

  @Test
  void deleteCourseDeletesWhenNoStudents() {
    // Arrange
    Course course = buildCourse(1L);
    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

    // Act
    courseService.deleteCourse(1L);

    // Assert
    verify(courseRepository).delete(course);
  }

  @Test
  void deleteCourseThrowsWhenStudentsEnrolled() {
    // Arrange
    Course course = buildCourse(1L);
    Student student = new Student();
    student.setId(1L);
    course.getStudents().add(student);
    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

    // Act + Assert
    assertThrows(ConflictException.class, () -> courseService.deleteCourse(1L));
    verify(courseRepository, never()).delete(any());
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
