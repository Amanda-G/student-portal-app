package com.amanda.studentportal.controller;

import com.amanda.studentportal.dto.StudentRequest;
import com.amanda.studentportal.dto.StudentResponse;
import com.amanda.studentportal.service.StudentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentController {

  private final StudentService studentService;

  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  @GetMapping
  public ResponseEntity<List<StudentResponse>> getStudents(
      @RequestParam(required = false) String search) {
    List<StudentResponse> students = studentService.getStudents(search);
    return ResponseEntity.ok(students);
  }

  @GetMapping("/{id}")
  public ResponseEntity<StudentResponse> getStudent(@PathVariable Long id) {
    StudentResponse student = studentService.getStudent(id);
    return ResponseEntity.ok(student);
  }

  @PostMapping
  public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
    StudentResponse created = studentService.createStudent(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id,
      @Valid @RequestBody StudentRequest request) {
    StudentResponse updated = studentService.updateStudent(id, request);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
    studentService.deleteStudent(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{studentId}/courses/{courseId}")
  public ResponseEntity<StudentResponse> enrollCourse(@PathVariable Long studentId,
      @PathVariable Long courseId) {
    StudentResponse student = studentService.enrollCourse(studentId, courseId);
    return ResponseEntity.ok(student);
  }

  @DeleteMapping("/{studentId}/courses/{courseId}")
  public ResponseEntity<StudentResponse> removeCourse(@PathVariable Long studentId,
      @PathVariable Long courseId) {
    StudentResponse student = studentService.removeCourse(studentId, courseId);
    return ResponseEntity.ok(student);
  }
}
