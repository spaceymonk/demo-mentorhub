package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@RequestMapping("/api/subjects")
@RestController
@AllArgsConstructor
public class ApiSubjects {

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final SubjectRepository subjectRepository;
    private final RoleRepository roleRepository;

    @RequestMapping(value = "/{id}", produces = "application/json", method = RequestMethod.GET)
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<Subject> getSubjectDetails(@PathVariable("id") String id) {
        Optional<Subject> subjectQuery = subjectRepository.findById(id);
        if (subjectQuery.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(subjectQuery.get());
        }
    }

    @RequestMapping(value = "/", consumes = "application/json", method = RequestMethod.POST)
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<String> saveSubjectDetails(@RequestBody Subject requestSubject) {

        // Data Control
        if (requestSubject.getMajorSubject() == null || requestSubject.getMajorSubject().isBlank()
                || requestSubject.getSubjects() == null) {
            return ResponseEntity.noContent().build();
        }
        requestSubject.getSubjects().removeIf(String::isBlank);
        if (requestSubject.getSubjects().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Data creation
        Subject subject = new Subject();
        if (requestSubject.getId() != null) {
            Optional<Subject> subjectQuery = subjectRepository.findById(requestSubject.getId());
            if (subjectQuery.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            subject = subjectQuery.get();
        }
        subject.setMajorSubject(requestSubject.getMajorSubject());
        subject.getSubjects().clear();
        subject.getSubjects().addAll(requestSubject.getSubjects());
        subjectRepository.save(subject);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<String> deleteSubject(@PathVariable("id") String id) {
        subjectRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
