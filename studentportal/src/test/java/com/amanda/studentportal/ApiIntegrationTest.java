package com.amanda.studentportal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amanda.studentportal.dto.CourseResponse;
import com.amanda.studentportal.dto.StudentRequest;
import com.amanda.studentportal.dto.StudentResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ApiIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void getStudentsReturnsSeedData() {
    // Act
    ResponseEntity<StudentResponse[]> response =
        restTemplate.getForEntity("/api/students", StudentResponse[].class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length >= 3);
  }

  @Test
  void getCoursesReturnsSeedData() {
    // Act
    ResponseEntity<CourseResponse[]> response =
        restTemplate.getForEntity("/api/courses", CourseResponse[].class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length >= 4);
  }

  @Test
  void createStudentReturns201() {
    // Arrange
    StudentRequest request = new StudentRequest("Test", "User", "test.user@gmail.com",
        LocalDate.of(2002, 1, 1));

    // Act
    ResponseEntity<StudentResponse> response =
        restTemplate.postForEntity("/api/students", request, StudentResponse.class);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().id());
    assertEquals("Test", response.getBody().firstName());
  }

  @Test
  void createStudentFailsValidation() {
    // Arrange
    StudentRequest request = new StudentRequest("", "User", "not-an-email",
        LocalDate.of(2030, 1, 1));

    // Act
    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/students", request, String.class);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("fieldErrors"));
  }

  @Test
  void enrollAndRemoveCourse() {
    // Arrange
    StudentRequest request = new StudentRequest("Enroll", "Tester", "enroll.tester@gmail.com",
        LocalDate.of(2002, 1, 1));
    ResponseEntity<StudentResponse> created =
        restTemplate.postForEntity("/api/students", request, StudentResponse.class);
    Long studentId = created.getBody().id();

    // Act
    ResponseEntity<StudentResponse> enrolled = restTemplate.postForEntity(
        "/api/students/" + studentId + "/courses/3", null, StudentResponse.class);

    // Assert
    assertEquals(HttpStatus.OK, enrolled.getStatusCode());
    assertEquals(1, enrolled.getBody().courses().size());

    // Act
    ResponseEntity<StudentResponse> removed = restTemplate.exchange(
        "/api/students/" + studentId + "/courses/3",
        org.springframework.http.HttpMethod.DELETE, null, StudentResponse.class);

    // Assert
    assertEquals(HttpStatus.OK, removed.getStatusCode());
    assertEquals(0, removed.getBody().courses().size());
  }
}
