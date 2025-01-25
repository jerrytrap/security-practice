package com.example.security.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByTitle(String title);

    Optional<Report> findFirstByOrderByIdDesc();
}
