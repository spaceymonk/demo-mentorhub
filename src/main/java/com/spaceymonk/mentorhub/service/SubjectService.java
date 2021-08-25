package com.spaceymonk.mentorhub.service;

import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public void validateSubject(Subject subject) {
        // Data Control
        if (subject.getMajorSubject() == null || subject.getMajorSubject().isBlank()
                || subject.getSubjects() == null || subject.getSubjects().removeIf(String::isBlank) || subject.getSubjects().isEmpty()) {
            throw new RuntimeException("No subjects entered!");
        }
    }

    public String saveSubjectToDb(Subject subject) {
        validateSubject(subject);
        // Data creation
        subjectRepository.save(subject);
        return subject.getId();
    }

    public String removeSubjectFromDb(String id) {
        subjectRepository.deleteById(id);
        return id;
    }
}
