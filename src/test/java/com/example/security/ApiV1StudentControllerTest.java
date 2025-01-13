package com.example.security;

import com.example.security.domain.student.ApiV1StudentController;
import com.example.security.domain.student.Student;
import com.example.security.domain.student.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ApiV1StudentControllerTest {
    @Autowired
    private StudentService studentService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("회원가입")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/students/join")
                                .content("""
                                        {
                                            "username": "new user",
                                            "password": "1234",
                                            "nickname": "무명"
                                        }
                                        """.stripIndent()
                                ).contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1StudentController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("무명님 환영합니다. 회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.createDate").isString())
                .andExpect(jsonPath("$.data.modifyDate").isString())
                .andExpect(jsonPath("$.data.nickname").value("무명"));

        Student student = studentService.findStudentByName("new user").get();
        assertThat(student.getNickname()).isEqualTo("무명");
    }
}
