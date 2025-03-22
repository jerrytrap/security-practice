package com.example.tdd.domain.student;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StudentDto {
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private String name;

    private String nickname;

    public StudentDto(Student student) {
        this.id = student.getId();
        this.createDate = student.getCreateDate();
        this.modifyDate = student.getModifiedDate();
        this.name = student.getName();
        this.nickname = student.getNickname();
    }
}