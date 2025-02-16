package com.example.security;

import com.example.security.domain.report.ApiV1ReportController;
import com.example.security.domain.report.Report;
import com.example.security.domain.report.ReportService;
import com.example.security.domain.student.Student;
import com.example.security.domain.student.StudentService;
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
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1ReportControllerTest {
    @Autowired
    private ReportService reportService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("1번글 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/reports/1")
                )
                .andDo(print());
        Report report = reportService.findById(1).get();
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(report.getId()))
                .andExpect(jsonPath("$.createDate").value(Matchers.startsWith(report.getCreateDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.modifyDate").value(Matchers.startsWith(report.getModifiedDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.authorId").value(report.getAuthor().getId()))
                .andExpect(jsonPath("$.authorName").value(report.getAuthor().getName()))
                .andExpect(jsonPath("$.title").value(report.getTitle()))
                .andExpect(jsonPath("$.content").value(report.getContent()));
    }

    @Test
    @DisplayName("존재하지 않는 1000000번글 조회, 404")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/reports/1000000")
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("글 작성")
    void t3() throws Exception {
        Student actor = studentService.findStudentByName("user1").get();
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/reports")
                                .header("Authorization", "Bearer " + actor.getApiKey())
                                .content("""
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        """)
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());
        Report report = reportService.findLatest().get();
        assertThat(report.getAuthor()).isEqualTo(actor);
        resultActions
                .andExpect(handler().methodName("create"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 작성되었습니다.".formatted(report.getId())))
                .andExpect(jsonPath("$.data.id").value(report.getId()))
                .andExpect(jsonPath("$.data.createDate").value(Matchers.startsWith(report.getCreateDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.modifyDate").value(Matchers.startsWith(report.getModifiedDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.authorId").value(report.getAuthor().getId()))
                .andExpect(jsonPath("$.data.authorName").value(report.getAuthor().getName()))
                .andExpect(jsonPath("$.data.title").value(report.getTitle()))
                .andExpect(jsonPath("$.data.content").value(report.getContent()));
    }

    @Test
    @DisplayName("글 작성, with no input")
    void t4() throws Exception {
        Student actor = studentService.findStudentByName("user1").get();
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/reports")
                                .header("Authorization", "Bearer " + actor.getApiKey())
                                .content("""
                                        {
                                            "title": "",
                                            "content": ""
                                        }
                                        """)
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        content-Length-length must be between 2 and 10000000
                        content-NotBlank-must not be blank
                        title-Length-length must be between 2 and 100
                        title-NotBlank-must not be blank
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("글 작성, with no actor")
    void t5() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/reports")
                                .content("""
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        """)
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("api key를 입력해주세요."));
    }

    @Test
    @DisplayName("글 수정")
    void t6() throws Exception {
        Student actor = studentService.findStudentByName("user1").get();
        Report report = reportService.findById(1).get();
        LocalDateTime oldModifyDate = report.getModifiedDate();
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/reports/1")
                                .header("Authorization", "Bearer " + actor.getApiKey())
                                .content("""
                                        {
                                            "title": "축구 하실 분 계신가요?",
                                            "content": "14시 까지 22명을 모아야 진행이 됩니다."
                                        }
                                        """)
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("1번 글이 수정되었습니다."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.createDate").value(Matchers.startsWith(report.getCreateDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.modifyDate").value(Matchers.not(oldModifyDate.toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.authorId").value(report.getAuthor().getId()))
                .andExpect(jsonPath("$.data.authorName").value(report.getAuthor().getName()))
                .andExpect(jsonPath("$.data.title").value("축구 하실 분 계신가요?"))
                .andExpect(jsonPath("$.data.content").value("14시 까지 22명을 모아야 진행이 됩니다."));
    }

    @Test
    @DisplayName("글 수정, with no input")
    void t7() throws Exception {
        Student actor = studentService.findStudentByName("user1").get();
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/reports/1")
                                .header("Authorization", "Bearer " + actor.getApiKey())
                                .content("""
                                        {
                                            "title": "",
                                            "content": ""
                                        }
                                        """)
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        content-Length-length must be between 2 and 10000000
                        content-NotBlank-must not be blank
                        title-Length-length must be between 2 and 100
                        title-NotBlank-must not be blank
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("글 수정, with no actor")
    void t8() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/reports/1")
                                .content("""
                                        {
                                            "title": "축구 하실 분 계신가요?",
                                            "content": "14시 까지 22명을 모아야 진행이 됩니다."
                                        }
                                        """)
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("api key를 입력해주세요."));
    }

    @Test
    @DisplayName("글 삭제")
    void t10() throws Exception {
        Student actor = studentService.findStudentByName("user1").get();
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/reports/1")
                                .header("Authorization", "Bearer " + actor.getApiKey())
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("1번 글이 삭제되었습니다."));

        assertThat(reportService.findById(1)).isEmpty();
    }

    @Test
    @DisplayName("글 삭제, with not exist post id")
    void t11() throws Exception {
        Student actor = studentService.findStudentByName("user1").get();
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/reports/1000000")
                                .header("Authorization", "Bearer " + actor.getApiKey())
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("글 삭제, with no actor")
    void t12() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/reports/1")
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("api key를 입력해주세요."));
    }

    @Test
    @DisplayName("글 삭제, with no permission")
    void t13() throws Exception {
        Student actor = studentService.findStudentByName("user2").get();
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/reports/1")
                                .header("Authorization", "Bearer " + actor.getApiKey())
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ReportController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-2"))
                .andExpect(jsonPath("$.msg").value("작성자만 글을 삭제할 수 있습니다."));
    }
}