package com.example.security.domain.student;

import com.example.security.global.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class ApiV1StudentController {
    private final StudentService studentService;

    record StudentJoinReqBody(
            String username,
            String password,
            String nickname
    ) {
    }

    @PostMapping("/join")
    public RsData<Void> join(
            @RequestBody StudentJoinReqBody reqBody
    ) {
        Student student = studentService.createStudent(reqBody.username, reqBody.password, reqBody.nickname);
        return new RsData<>("201-1", "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(student.getNickname()));
    }
}
