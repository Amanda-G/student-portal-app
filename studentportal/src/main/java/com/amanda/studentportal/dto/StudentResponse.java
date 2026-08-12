package com.amanda.studentportal.dto;

import java.time.LocalDate;
import java.util.List;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDate dateOfBirth,
        List<CourseSummary> courses) {
}
