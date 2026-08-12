package com.amanda.studentportal.repository;

import com.amanda.studentportal.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
