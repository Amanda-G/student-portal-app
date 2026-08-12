package com.amanda.studentportal.service;

import com.amanda.studentportal.dto.CourseRequest;
import com.amanda.studentportal.dto.CourseResponse;
import com.amanda.studentportal.dto.StudentSummary;
import com.amanda.studentportal.entity.Course;
import com.amanda.studentportal.exception.ConflictException;
import com.amanda.studentportal.exception.NotFoundException;
import com.amanda.studentportal.repository.CourseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final CourseRepository courseRepository;

  public List<CourseResponse> getCourses(String search) {
    List<Course> courses;
    if (search == null || search.isBlank()) {
      courses = courseRepository.findAll();
    } else {
      courses = courseRepository
          .findByCourseCodeContainingIgnoreCaseOrCourseNameContainingIgnoreCase(search, search);
    }
    return courses.stream().map(this::toResponse).toList();
  }

  public CourseResponse createCourse(CourseRequest request) {
    boolean codeTaken = courseRepository.existsByCourseCodeIgnoreCase(request.courseCode());
    if (codeTaken) {
      throw new ConflictException("Course code " + request.courseCode() + " already exists");
    }
    Course course = new Course();
    course.setCourseCode(request.courseCode());
    course.setCourseName(request.courseName());
    course.setDescription(request.description());
    Course saved = courseRepository.save(course);
    return toResponse(saved);
  }

  public CourseResponse getCourse(Long id) {
    Course course = findCourse(id);
    return toResponse(course);
  }

  private Course findCourse(Long id) {
    return courseRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Course not found with id " + id));
  }

  private CourseResponse toResponse(Course course) {
    List<StudentSummary> students = course.getStudents().stream()
        .map(s -> new StudentSummary(
            s.getId(),
            s.getFirstName(),
            s.getLastName(),
            s.getEmail())
        )
        .toList();
    return new CourseResponse(
        course.getId(),
        course.getCourseCode(),
        course.getCourseName(),
        course.getDescription(),
        students);
  }
}
