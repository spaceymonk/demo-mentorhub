package com.spaceymonk.mentorhub.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @GetMapping("/subjects")
    @RolesAllowed({"ROLE_ADMIN"})
    public String subjectsPage(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());
        model.addAttribute("currentUser", currentUser);

        List<Subject> subjectList = subjectRepository.findAll();
        model.addAttribute("subjectList", subjectList);

        return "features/subject-editor";
    }

    @GetMapping(value = "/subjects/{id}", produces = "application/json")
    @RolesAllowed({"ROLE_ADMIN"})
    @ResponseBody
    public ResponseEntity<Subject> getSubjectDetails(@PathVariable("id") String id) {
        Optional<Subject> subjectQuery = subjectRepository.findById(id);
        if (subjectQuery.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(subjectQuery.get());
        }
    }

    @PostMapping(value = "/subjects", consumes = "application/json")
    @RolesAllowed({"ROLE_ADMIN"})
    @ResponseBody
    public ResponseEntity<String> saveSubjectDetails(@RequestBody Subject requestSubject) {
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
        return ResponseEntity.ok("nice");
    }

    @DeleteMapping(value = "/subjects/{id}", produces = "application/json")
    @RolesAllowed({"ROLE_ADMIN"})
    @ResponseBody
    public ResponseEntity<String> deleteSubject(@PathVariable("id") String id) {
        subjectRepository.deleteById(id);
        return ResponseEntity.ok("nice");
    }

}
