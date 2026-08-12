package com.amanda.studentportal.repository;

import com.amanda.studentportal.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByCourseCodeContainingIgnoreCaseOrCourseNameContainingIgnoreCase(String courseCode, String courseName);

}
