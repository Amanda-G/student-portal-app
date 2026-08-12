package com.amanda.studentportal.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
      Room for improvement to handle exceptions more granular.
      like  ConflictException, NotFoundException etc.,
  * */

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    }
    ErrorResponse body = new ErrorResponse(400, "Validation failed", fieldErrors, Instant.now());
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleOther(Exception ex) {
    log.error("Unexpected error", ex);
    ErrorResponse body = new ErrorResponse(500, "Something went wrong", null, Instant.now());
    return ResponseEntity.internalServerError().body(body);
  }
}
