package com.example.tdd.domain.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByOrderByIdDesc();

    Optional<Student> findByName(String name);

    Optional<Student> findByApiKey(String apiKey);
}