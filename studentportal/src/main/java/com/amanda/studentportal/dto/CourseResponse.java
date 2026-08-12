package com.amanda.studentportal.dto;

import java.util.List;

public record CourseResponse(
    Long id,
    String courseCode,
    String courseName,
    String description,
    List<StudentSummary> students) {
}
