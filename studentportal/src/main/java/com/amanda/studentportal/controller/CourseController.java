package com.amanda.studentportal.controller;

import com.amanda.studentportal.dto.CourseResponse;
import com.amanda.studentportal.service.CourseService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getCourses(@RequestParam(required = false) String search) {
        List<CourseResponse> courses = courseService.getCourses(search);
        return ResponseEntity.ok(courses);
    }
}
