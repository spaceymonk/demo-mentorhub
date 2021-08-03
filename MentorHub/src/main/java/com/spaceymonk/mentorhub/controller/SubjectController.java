package com.spaceymonk.mentorhub.controller;

import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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


}
