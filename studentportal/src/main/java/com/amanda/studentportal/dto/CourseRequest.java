package com.amanda.studentportal.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseRequest(
    @NotBlank(message = "Course code is required") String courseCode,
    @NotBlank(message = "Course name is required") String courseName,
    String description) {

}
