package com.example.tdd.domain.report.comment;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDto {
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime modifiedDate;

    private long reportId;

    private long authorId;

    private String authorName;

    private String content;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.createDate = comment.getCreateDate();
        this.modifiedDate = comment.getModifiedDate();
        this.reportId = comment.getReport().getId();
        this.authorId = comment.getAuthor().getId();
        this.authorName = comment.getAuthor().getName();
        this.content = comment.getContent();
    }
}