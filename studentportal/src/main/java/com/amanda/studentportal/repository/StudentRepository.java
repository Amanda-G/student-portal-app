package com.amanda.studentportal.repository;

import com.amanda.studentportal.entity.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

  List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName,
      String lastName);

}
