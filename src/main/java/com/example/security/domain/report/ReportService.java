package com.example.security.domain.report;

import com.example.security.domain.student.Student;
import com.example.security.global.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public long count() {
        return reportRepository.count();
    }

    public Report create(Student author, String title, String content, boolean published, boolean listed) {
        reportRepository.findByTitle(title)
                .ifPresent(_ -> {
                    throw new ServiceException("400-1", "Report already exists");
                });

        Report report = Report.builder()
                .author(author)
                .title(title)
                .content(content)
                .published(published)
                .listed(listed)
                .build();

        return reportRepository.save(report);
    }

    public List<Report> findAllByOrderByIdDesc() {
        return reportRepository.findAllByOrderByIdDesc();
    }

    public Optional<Report> findById(long id) {
        return reportRepository.findById(id);
    }

    public void modify(Report report, String title, String content, boolean published, boolean listed) {
        report.setTitle(title);
        report.setContent(content);
        report.setPublished(published);
        report.setListed(listed);
        reportRepository.save(report);
    }

    public void delete(Report report) {
        reportRepository.delete(report);
    }

    public void flush() {
        reportRepository.flush();
    }

    public Optional<Report> findLatest() {
        return reportRepository.findFirstByOrderByIdDesc();
    }

    public Page<Report> findByListedPaged(boolean listed, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        return reportRepository.findByListed(listed, pageRequest);
    }
}