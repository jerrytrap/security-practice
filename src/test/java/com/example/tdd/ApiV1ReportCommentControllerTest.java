package com.example.tdd;

import com.example.tdd.domain.report.Report;
import com.example.tdd.domain.report.ReportService;
import com.example.tdd.domain.report.comment.Comment;
import com.example.tdd.domain.report.comment.controller.ApiV1ReportCommentController;
import com.example.tdd.domain.student.Student;
import com.example.tdd.domain.student.StudentService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1ReportCommentControllerTest {
    @Autowired
    private ReportService reportService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("다건 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/reports/1/comments")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ReportCommentController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk());

        List<Comment> comments = reportService
                .findById(1).get().getComments();

        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(comment.getId()))
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(Matchers.startsWith(comment.getCreateDate().toString().substring(0, 25))))
                    .andExpect(jsonPath("$[%d].modifiedDate".formatted(i)).value(Matchers.startsWith(comment.getModifiedDate().toString().substring(0, 25))))
                    .andExpect(jsonPath("$[%d].authorId".formatted(i)).value(comment.getAuthor().getId()))
                    .andExpect(jsonPath("$[%d].authorName".formatted(i)).value(comment.getAuthor().getName()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(comment.getContent()));
        }
    }

    @Test
    @DisplayName("댓글 삭제")
    void t2() throws Exception {
        Student actor = studentService.findStudentByName("user2").get();

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/reports/1/comments/1")
                                .header("Authorization", "Bearer " + actor.getApiKey())
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ReportCommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("1번 댓글이 삭제되었습니다."));
    }

    @Test
    @DisplayName("댓글 수정")
    void t3() throws Exception {
        Student actor = studentService.findStudentByName("user2").get();

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/reports/1/comments/1")
                                .header("Authorization", "Bearer " + actor.getApiKey())
                                .content("""
                                         {
                                             "content": "내용 new"
                                         }
                                         """)
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ReportCommentController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("1번 댓글이 수정되었습니다."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.createDate").exists())
                .andExpect(jsonPath("$.data.modifiedDate").exists())
                .andExpect(jsonPath("$.data.authorId").value(actor.getId()))
                .andExpect(jsonPath("$.data.authorName").value(actor.getName()))
                .andExpect(jsonPath("$.data.content").value("내용 new"));
    }

    @Test
    @DisplayName("댓글 등록")
    void t4() throws Exception {
        Student actor = studentService.findStudentByName("user2").get();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/reports/1/comments")
                                .header("Authorization", "Bearer " + actor.getApiKey())
                                .content("""
                                         {
                                             "content": "내용 new"
                                         }
                                         """)
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        Report report = reportService.findById(1).get();
        Comment comment = report.getComments().getLast();

        resultActions
                .andExpect(handler().handlerType(ApiV1ReportCommentController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글이 생성되었습니다.".formatted(comment.getId())))
                .andExpect(jsonPath("$.data.id").value(comment.getId()))
                .andExpect(jsonPath("$.data.createDate").value(Matchers.startsWith(comment.getCreateDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.modifiedDate").value(Matchers.startsWith(comment.getModifiedDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.authorId").value(comment.getAuthor().getId()))
                .andExpect(jsonPath("$.data.authorName").value(comment.getAuthor().getName()))
                .andExpect(jsonPath("$.data.content").value(comment.getContent()));
    }
}