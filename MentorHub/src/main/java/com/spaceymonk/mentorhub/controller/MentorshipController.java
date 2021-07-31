package com.spaceymonk.mentorhub.controller;

import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class MentorshipController {

    private final SubjectRepository subjectRepository;

    @GetMapping("/apply")
    public String applyPage(Model model) {
        List<Subject> categories = subjectRepository.findAll();
        model.addAttribute("categories", categories);
        return "features/mentor-application";
    }

    @PostMapping("/apply")
    public String registerApplication(@RequestParam("selectedCategory") String selectedCategory,
                                      @RequestParam("selectedSubject") List<String> selectedSubjects,
                                      @RequestParam("explainMsg") String explainMsg) {
        System.out.println(selectedCategory);
        for (String s : selectedSubjects)
            System.out.println(s);
        System.out.println(explainMsg);
        return "redirect:/";
    }

}
