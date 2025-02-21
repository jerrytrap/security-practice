package com.example.security.domain.report;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReportWithContentDto {
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private long authorId;

    private String authorName;

    private String title;

    private String content;

    private boolean published;

    private boolean listed;

    public ReportWithContentDto(Report report) {
        this.id = report.getId();
        this.createDate = report.getCreateDate();
        this.modifyDate = report.getModifiedDate();
        this.authorId = report.getAuthor().getId();
        this.authorName = report.getAuthor().getName();
        this.title = report.getTitle();
        this.content = report.getContent();
        this.published = report.isPublished();
        this.listed = report.isListed();
    }
}