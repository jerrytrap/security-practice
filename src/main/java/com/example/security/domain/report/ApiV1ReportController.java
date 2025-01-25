package com.example.security.domain.report;

import com.example.security.domain.student.Student;
import com.example.security.global.Rq;
import com.example.security.global.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ApiV1ReportController {
    private final ReportService reportService;
    private final Rq rq;

    @GetMapping("/{id}")
    public ReportDto item(@PathVariable Long id) {
        return new ReportDto(reportService.findById(id).get());
    }

    record PostWriteReqBody(
            @NotBlank
            @Length(min = 2)
            String title,
            @NotBlank
            @Length(min = 2)
            String content
    ) {
    }
    @PostMapping
    public RsData<ReportDto> create(
            @RequestBody @Valid PostWriteReqBody reqBody
    ) {
        Student actor = rq.checkAuthentication();
        Report report = reportService.create(actor, reqBody.title, reqBody.content);
        return new RsData<>(
                "201-1",
                "%d번 글이 작성되었습니다.".formatted(report.getId()),
                new ReportDto(report)
        );
    }
}
