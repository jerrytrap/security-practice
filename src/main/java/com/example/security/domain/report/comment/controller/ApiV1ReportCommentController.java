package com.example.security.domain.report.comment.controller;


import com.example.security.domain.report.Report;
import com.example.security.domain.report.ReportService;
import com.example.security.domain.report.comment.Comment;
import com.example.security.domain.report.comment.CommentDto;
import com.example.security.domain.student.Student;
import com.example.security.global.Rq;
import com.example.security.global.RsData;
import com.example.security.global.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports/{reportId}/comments")
@RequiredArgsConstructor
public class ApiV1ReportCommentController {
    private final ReportService reportService;
    private final Rq rq;

    @GetMapping
    @Transactional(readOnly = true)
    public List<CommentDto> items(
            @PathVariable long reportId
    ) {
        Report report = reportService.findById(reportId).
            orElseThrow(() -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(reportId)));

        return report
                .getComments()
                .stream()
                .map(CommentDto::new)
                .toList();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public RsData<Void> delete(
            @PathVariable long reportId,
            @PathVariable long id
    ) {
        Student actor = rq.checkAuthentication();

        Report report = reportService.findById(reportId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(reportId))
        );

        Comment comment = report.getCommentById(id).orElseThrow(
                () -> new ServiceException("404-2", "%d번 댓글은 존재하지 않습니다.".formatted(id))
        );

        comment.checkActorCanDelete(actor);

        report.removeComment(comment);

        return new RsData<>(
                "200-1",
                "%d번 댓글이 삭제되었습니다.".formatted(id)
        );
    }

    record CommentModifyReqBody(
            @NotBlank
            @Length(min = 2, max = 100)
            String content
    ) {
    }

    @PutMapping("/{id}")
    @Transactional
    public RsData<CommentDto> modify(
            @PathVariable long reportId,
            @PathVariable long id,
            @RequestBody @Valid CommentModifyReqBody reqBody
    ) {
        Student actor = rq.checkAuthentication();

        Report report = reportService.findById(reportId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(reportId))
        );

        Comment comment = report.getCommentById(id).orElseThrow(
                () -> new ServiceException("404-2", "%d번 댓글은 존재하지 않습니다.".formatted(id))
        );

        comment.checkActorCanModify(actor);

        comment.modify(reqBody.content);

        return new RsData<>(
                "200-1",
                "%d번 댓글이 수정되었습니다.".formatted(id),
                new CommentDto(comment)
        );
    }

    record CommentWriteReqBody(
            @NotBlank
            @Length(min = 2, max = 100)
            String content
    ) {
    }

    @PostMapping
    @Transactional
    public RsData<CommentDto> write(
            @PathVariable long reportId,
            @RequestBody @Valid CommentWriteReqBody reqBody
    ) {
        Student actor = rq.checkAuthentication();

        Report report = reportService.findById(reportId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(reportId))
        );

        Comment comment = report.addComment(
                actor,
                reqBody.content
        );

        reportService.flush();

        return new RsData<>(
                "201-1",
                "%d번 댓글이 생성되었습니다.".formatted(comment.getId()),
                new CommentDto(comment)
        );
    }
}