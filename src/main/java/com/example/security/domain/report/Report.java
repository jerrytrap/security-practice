package com.example.security.domain.report;

import com.example.security.domain.report.comment.Comment;
import com.example.security.domain.student.Student;
import com.example.security.global.BaseTime;
import com.example.security.global.ServiceException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report extends BaseTime {
    @Column(length = 20)
    private String title;

    @Column(length = 50)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Student author;

    @OneToMany(mappedBy = "report", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    private boolean published;

    private boolean listed;

    public Comment addComment(Student author, String content) {
        Comment comment = Comment.builder()
                .report(this)
                .author(author)
                .content(content)
                .build();

        comments.add(comment);
        return comment;
    }

    public List<Comment> getCommentsByOrderByIdDesc() {
        return comments.reversed();
    }

    public Optional<Comment> getCommentById(long id) {
        return comments
                .stream()
                .filter(comment -> comment.getId() == id)
                .findFirst();
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    public void checkActorCanDelete(Student actor) {
        if (actor == null) throw new ServiceException("403-1", "로그인 후 이용해주세요.");

        if (actor.isAdmin()) return;

        if (actor.equals(author)) return;

        throw new ServiceException("403-1", "작성자만 글을 삭제할 수 있습니다.");
    }

    public void checkActorCanModify(Student actor) {
        if (actor == null) throw new ServiceException("401-1", "로그인 후 이용해주세요.");

        if (actor.equals(author)) return;

        throw new ServiceException("403-1", "작성자만 글을 수정할 수 있습니다.");
    }

    public void checkActorCanRead(Student actor) {
        if (actor == null) throw new ServiceException("401-1", "로그인 후 이용해주세요.");

        if (actor.isAdmin()) return;

        if (actor.equals(author)) return;

        throw new ServiceException("403-1", "비공개글은 작성자만 볼 수 있습니다.");
    }
}