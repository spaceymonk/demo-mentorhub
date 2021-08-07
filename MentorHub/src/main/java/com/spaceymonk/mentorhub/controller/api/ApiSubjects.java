package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RequestMapping("/api/subjects")
@RestController
@AllArgsConstructor
public class ApiSubjects {

    private final SubjectRepository subjectRepository;

    @RequestMapping(value = "/", consumes = "application/json", method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<String> saveSubjectDetails(@RequestBody Subject subject) {

        // Data Control
        if (subject.getMajorSubject() == null || subject.getMajorSubject().isBlank()
                || subject.getSubjects() == null || subject.getSubjects().removeIf(String::isBlank) || subject.getSubjects().isEmpty()) {
            return ResponseEntity.badRequest().body("No subjects entered!");
        }

        // Data creation
        subjectRepository.save(subject);
        return ResponseEntity.ok(subject.getId());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<String> deleteSubject(@PathVariable("id") String id) {
        subjectRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }
}
