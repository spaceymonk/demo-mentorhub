package com.spaceymonk.mentorhub.controller;

import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Controller
@AllArgsConstructor
public class MentorshipRequestController {

    private final SubjectRepository subjectRepository;

    @GetMapping("/apply")
    @RolesAllowed({"ROLE_USER"})
    public String applyPage(Model model) {
        List<Subject> categories = subjectRepository.findAll();
        model.addAttribute("categories", categories);
        return "features/mentor-application";
    }

}
