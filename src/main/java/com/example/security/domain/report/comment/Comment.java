package com.example.security.domain.report.comment;

import com.example.security.domain.report.Report;
import com.example.security.domain.student.Student;
import com.example.security.global.BaseTime;
import com.example.security.global.ServiceException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    private Student author;

    @Column(columnDefinition = "TEXT")
    private String content;

    public void modify(String content) {
        this.content = content;
    }

    public void checkActorCanDelete(Student actor) {
        if (actor == null) throw new ServiceException("403-1", "로그인 후 이용해주세요.");

        if (actor.isAdmin()) return;

        if (actor.equals(author)) return;

        throw new ServiceException("403-2", "작성자만 댓글을 삭제할 수 있습니다.");
    }

    public void checkActorCanModify(Student actor) {
        if (actor == null) throw new ServiceException("403-1", "로그인 후 이용해주세요.");

        if (actor.equals(author)) return;

        throw new ServiceException("403-2", "작성자만 댓글을 수정할 수 있습니다.");
    }
}
