package com.example.security;

import com.example.security.domain.report.ReportService;
import com.example.security.domain.report.comment.Comment;
import com.example.security.domain.report.comment.controller.ApiV1ReportCommentController;
import com.example.security.domain.student.StudentService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}