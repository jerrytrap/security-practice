package com.example.security.domain.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByTitle(String title);

    Optional<Report> findFirstByOrderByIdDesc();

    List<Report> findAllByOrderByIdDesc();

    Page<Report> findByListed(boolean listed, Pageable pageable);
}