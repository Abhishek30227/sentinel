package com.sentinel.repo;

import com.sentinel.module.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByRollNo(String rollNo);

}
