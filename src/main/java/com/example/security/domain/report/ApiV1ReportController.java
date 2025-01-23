package com.example.security.domain.report;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ApiV1ReportController {
    private final ReportService reportService;

    @GetMapping("/{id}")
    public ReportDto item(@PathVariable Long id) {
        return new ReportDto(reportService.findById(id).get());
    }
}
