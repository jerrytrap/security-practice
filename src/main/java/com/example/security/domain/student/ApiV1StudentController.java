package com.example.security.domain.student;

import com.example.security.global.Rq;
import com.example.security.global.RsData;
import com.example.security.global.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class ApiV1StudentController {
    private final Rq rq;
    private final StudentService studentService;

    record StudentJoinReqBody(
            @NotBlank
            String username,
            @NotBlank
            String password,
            @NotBlank
            String nickname
    ) {
    }

    @PostMapping("/join")
    public RsData<StudentDto> join(
            @RequestBody @Valid StudentJoinReqBody reqBody
    ) {
        Student student = studentService.createStudent(reqBody.username, reqBody.password, reqBody.nickname);
        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(student.getNickname()),
                new StudentDto(student)
        );
    }

    record StudentLoginReqBody(
            @NotBlank
            String username,
            @NotBlank
            String password
    ) {
    }
    record StudentLoginResBody(
            StudentDto item,
            String apiKey
    ) {
    }
    @PostMapping("/login")
    public RsData<StudentLoginResBody> login(
            @RequestBody @Valid StudentLoginReqBody reqBody
    ) {
        Student student = studentService
                .findStudentByName(reqBody.username)
                .orElseThrow(() -> new ServiceException("401-1", "존재하지 않는 사용자입니다."));

        if (!student.matchPassword(reqBody.password))
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(student.getName()),
                new StudentLoginResBody(
                        new StudentDto(student),
                        student.getApiKey()
                )
        );
    }

    @GetMapping("/me")
    public StudentDto me() {
        Student student = rq.checkAuthentication();

        return new StudentDto(student);
    }
}
