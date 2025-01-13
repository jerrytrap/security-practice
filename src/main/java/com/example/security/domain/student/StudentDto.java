package com.example.security.domain.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StudentDto {
    private long id;

    @JsonProperty("createdDateTime")
    private LocalDateTime createDate;

    @JsonProperty("modifiedDateTime")
    private LocalDateTime modifyDate;

    private String name;

    private Integer age;

    public StudentDto(Student student) {
        this.id = student.getId();
        this.createDate = student.getCreateDate();
        this.modifyDate = student.getModifiedDate();
        this.name = student.getName();
    }
}
