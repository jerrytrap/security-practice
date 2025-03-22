package com.example.tdd.domain.report;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReportDto {
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private long authorId;

    private String authorName;

    private String title;

    private boolean published;

    private boolean listed;

    public ReportDto(Report report) {
        this.id = report.getId();
        this.createDate = report.getCreateDate();
        this.modifyDate = report.getModifiedDate();
        this.authorId = report.getAuthor().getId();
        this.authorName = report.getAuthor().getName();
        this.title = report.getTitle();
        this.published = report.isPublished();
        this.listed = report.isListed();
    }
}