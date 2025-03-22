package com.example.tdd.domain.student;

import com.example.tdd.global.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public long getCount() {
        return studentRepository.count();
    }

    public Student createStudent(String name, String password, String nickname) {
        studentRepository
                .findByName(name)
                .ifPresent(_ -> {
                    throw new ServiceException("409-1", "해당 username은 이미 사용중입니다.");
                });

        Student student = Student.builder()
                .name(name)
                .password(password)
                .nickname(nickname)
                .apiKey(UUID.randomUUID().toString())
                .build();

        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAllByOrderByIdDesc();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElseThrow();
    }

    public void delete(Student student) {
        studentRepository.delete(student);
    }

    public void modify(Student student, String name) {
        student.setName(name);
    }

    public Optional<Student> findStudentByName(String name) {
        return studentRepository.findByName(name);
    }

    public Optional<Student> findStudentById(long studentId) {
        return studentRepository.findById(studentId);
    }

    public Optional<Student> findStudentByApiKey(String apiKey) {
        return studentRepository.findByApiKey(apiKey);
    }
}