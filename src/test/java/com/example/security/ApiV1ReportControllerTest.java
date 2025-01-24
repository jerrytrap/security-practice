package com.example.security;

import com.example.security.domain.report.ApiV1ReportController;
import com.example.security.domain.report.Report;
import com.example.security.domain.report.ReportService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}