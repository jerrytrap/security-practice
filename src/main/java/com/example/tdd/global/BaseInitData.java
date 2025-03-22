package com.example.tdd.global;

import com.example.tdd.domain.report.Report;
import com.example.tdd.domain.report.ReportService;
import com.example.tdd.domain.student.Student;
import com.example.tdd.domain.student.StudentService;
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

    @Transactional
    public void createSampleStudents() {
        if (studentService.getCount() > 0) return;

        Student student1 = studentService.createStudent("user1", "1234", "이름1");
        if (AppConfig.isNotProd()) student1.setApiKey("user1");

        Student student2 = studentService.createStudent("user2", "1234", "이름2");
        if (AppConfig.isNotProd()) student2.setApiKey("user2");

        Student student3 = studentService.createStudent("user3", "1234", "이름3");
        if (AppConfig.isNotProd()) student3.setApiKey("user3");

    }

    @Transactional
    public void createSampleReports() {
        if (reportService.count() > 0) return;

        Student student1 = studentService.findStudentByName("user1").get();
        Student student2 = studentService.findStudentByName("user2").get();
        Student student3 = studentService.findStudentByName("user3").get();

        Report report1 = reportService.create(student1, "보고서1", "내용1", true, true);
        report1.addComment(student2, "확인");
        report1.addComment(student3, "다시");

        Report report2 = reportService.create(student1, "보고서2", "내용2", true, false);
        Report report3 = reportService.create(student1, "보고서3", "내용3", false, true);
    }
}