package com.spaceymonk.mentorhub.controller;

import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Controller
@AllArgsConstructor
public class MentorshipController {

    private final SubjectRepository subjectRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    @GetMapping("/apply")
    @RolesAllowed({"ROLE_USER"})
    public String applyPage(Model model) {
        List<Subject> categories = subjectRepository.findAll();
        model.addAttribute("categories", categories);
        return "features/mentor-application";
    }

    @PostMapping("/apply")
    @RolesAllowed({"ROLE_USER"})
    public String registerApplication(@RequestParam(value = "selectedCategory", required = false) String selectedCategory,
                                      @RequestParam(value = "selectedSubject", required = false) List<String> selectedSubjects,
                                      @RequestParam(value = "explainMsg", required = false) String explainMsg,
                                      Model model) {
        List<Subject> categories = subjectRepository.findAll();
        model.addAttribute("categories", categories);

        // check for given category
        if (selectedCategory == null || selectedCategory.isBlank()) {
            model.addAttribute("errorTxt", "Please select a major!");
            return "features/mentor-application";
        }
        Subject s = subjectRepository.findByMajorSubject(selectedCategory);
        if (s == null) {
            model.addAttribute("errorTxt", "No such major found!");
            return "features/mentor-application";
        }

        // check for subjects
        if (selectedSubjects == null || selectedSubjects.isEmpty()) {
            model.addAttribute("errorTxt", "No subject entered!");
            return "features/mentor-application";
        }
        if (!s.getSubjects().containsAll(selectedSubjects)) {
            model.addAttribute("errorTxt", "Selected subjects does not belong to the selected major!");
            return "features/mentor-application";
        }

        // check for explain message
        if (explainMsg == null || explainMsg.isBlank()) {
            model.addAttribute("errorTxt", "Please write something about yourself.");
            return "features/mentor-application";
        }

//        todo: save to database
//        requestRepository.save(new Request());

        model.addAttribute("successTxt", "Your application successfully sent.");
        return "features/mentor-application";
    }

}
