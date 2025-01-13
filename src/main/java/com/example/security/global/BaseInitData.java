package com.example.security.global;

import com.example.security.domain.report.Report;
import com.example.security.domain.report.ReportService;
import com.example.security.domain.student.Student;
import com.example.security.domain.student.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    private final StudentService studentService;
    private final ReportService reportService;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.createSampleStudents();
            self.createSampleReports();
        };
    }

    @jakarta.transaction.Transactional
    public void createSampleStudents() {
        if (studentService.getCount() > 0) return;

        Student student1 = studentService.createStudent("이름1", 20, "1234");
        if (AppConfig.isNotProd()) student1.setApiKey("user1");

        Student student2 = studentService.createStudent("이름2", 30, "1234");
        if (AppConfig.isNotProd()) student2.setApiKey("user2");

        Student student3 = studentService.createStudent("이름3", 40, "1234");
        if (AppConfig.isNotProd()) student3.setApiKey("user3");

    }

    @Transactional
    public void createSampleReports() {
        if (reportService.count() > 0) return;

        Student student1 = studentService.findStudentByName("이름1").get();
        Student student2 = studentService.findStudentByName("이름2").get();
        Student student3 = studentService.findStudentByName("이름3").get();

        Report report1 = reportService.create(student1, "보고서1", "내용1");
        report1.addComment(student2, "확인");
        report1.addComment(student3, "다시");

        Report report2 = reportService.create(student1, "보고서2", "내용2");
        Report report3 = reportService.create(student2, "보고서3", "내용3");
    }
}

