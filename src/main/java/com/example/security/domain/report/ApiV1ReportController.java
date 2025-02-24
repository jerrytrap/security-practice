package com.example.security.domain.report;

import com.example.security.domain.student.Student;
import com.example.security.global.Rq;
import com.example.security.global.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ApiV1ReportController {
    private final ReportService reportService;
    private final Rq rq;

    @GetMapping
    public Map<String, Object> items(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Page<Report> reportPage = reportService.findByListedPaged(true, page, pageSize);

        long totalItems = reportPage.getTotalElements();

        List<ReportDto> items = reportPage
                .getContent()
                .stream()
                .map(ReportDto::new)
                .toList();

        long totalPages = reportPage.getTotalPages();

        return Map.of(
                "totalItems", totalItems,
                "items", items,
                "totalPages", totalPages,
                "currentPageNumber", page,
                "pageSize", pageSize
        );
    }

    @GetMapping("/{id}")
    public ReportWithContentDto item(@PathVariable Long id) {
        Report report = reportService.findById(id).get();

        if (!report.isPublished()) {
            Student actor = rq.checkAuthentication();
            report.checkActorCanRead(actor);
        }
        return new ReportWithContentDto(report);
    }

    record PostWriteReqBody(
            @NotBlank
            @Length(min = 2, max = 100)
            String title,
            @NotBlank
            @Length(min = 2, max = 10000000)
            String content,
            boolean published,
            boolean listed
    ) {
    }

    @PostMapping
    public RsData<ReportWithContentDto> create(
            @RequestBody @Valid PostWriteReqBody reqBody
    ) {
        Student actor = rq.checkAuthentication();
        Report report = reportService.create(actor, reqBody.title, reqBody.content, reqBody.published, reqBody.listed);
        return new RsData<>(
                "201-1",
                "%d번 글이 작성되었습니다.".formatted(report.getId()),
                new ReportWithContentDto(report)
        );
    }
    record PostModifyReqBody(
            @NotBlank
            @Length(min = 2, max = 100)
            String title,
            @NotBlank
            @Length(min = 2, max = 10000000)
            String content,
            boolean published,
            boolean listed
    ) {
    }
    @PutMapping("/{id}")
    public RsData<ReportWithContentDto> modify(
            @PathVariable long id,
            @RequestBody @Valid PostModifyReqBody reqBody
    ) {
        Student actor = rq.checkAuthentication();
        Report report = reportService.findById(id).get();
        report.checkActorCanModify(actor);
        reportService.modify(report, reqBody.title, reqBody.content, reqBody.published, reqBody.listed);
        reportService.flush();

        return new RsData<>(
                "200-1",
                "%d번 글이 수정되었습니다.".formatted(id),
                new ReportWithContentDto(report)
        );
    }

    @DeleteMapping("/{id}")
    public RsData<Void> delete(
            @PathVariable long id
    ) {
        Student member = rq.checkAuthentication();
        Report report = reportService.findById(id).get();
        report.checkActorCanDelete(member);
        reportService.delete(report);
        return new RsData<>("200-1", "%d번 글이 삭제되었습니다.".formatted(id));
    }
}