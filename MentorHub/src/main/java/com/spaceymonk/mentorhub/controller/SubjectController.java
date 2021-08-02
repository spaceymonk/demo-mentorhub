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

    @GetMapping(value = "/subjects/{majorName}", produces = "application/json")
    @RolesAllowed({"ROLE_ADMIN"})
    @ResponseBody
    public ResponseEntity<Subject> getSubjectDetails(@PathVariable("majorName") String majorName) throws JsonProcessingException {
        Subject subject = subjectRepository.findByMajorSubject(majorName);
        if (subject == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(subject);
        }
    }

    @PostMapping(value = "/subjects/save", consumes = "application/json")
    @RolesAllowed({"ROLE_ADMIN"})
    @ResponseBody
    public ResponseEntity<String> saveSubjectDetails(@RequestBody Subject requestSubject) {
        Subject subject = subjectRepository.findByMajorSubject(requestSubject.getMajorSubject());
        if (subject == null)
            subject = new Subject();
        subject.setMajorSubject(requestSubject.getMajorSubject());
        subject.getSubjects().clear();
        subject.getSubjects().addAll(requestSubject.getSubjects());
        subjectRepository.save(subject);
        return ResponseEntity.ok("nice");
    }

}
