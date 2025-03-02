package com.example.security.domain.report.comment.controller;


import com.example.security.domain.report.Report;
import com.example.security.domain.report.ReportService;
import com.example.security.domain.report.comment.CommentDto;
import com.example.security.global.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports/{reportId}/comments")
@RequiredArgsConstructor
public class ApiV1ReportCommentController {
    private final ReportService reportService;
    private final Rq rq;

    @GetMapping
    public List<CommentDto> items(
            @PathVariable long reportId
    ) {
        Report report = reportService.findById(reportId).get();

        return report
                .getComments()
                .stream()
                .map(CommentDto::new)
                .toList();
    }
}