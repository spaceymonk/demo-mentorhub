package com.spaceymonk.mentorhub.controller;

import com.spaceymonk.mentorhub.controller.wrapper.RegisterApplicationWrapper;
import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.security.RolesAllowed;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class MentorshipRequestController {

    private final SubjectRepository subjectRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    @GetMapping("/apply")
    @RolesAllowed({"ROLE_USER"})
    public String applyPage(Model model) {
        List<Subject> categories = subjectRepository.findAll();
        model.addAttribute("categories", categories);
        return "features/mentor-application";
    }

    @PostMapping(value = "/apply", consumes = "application/json")
    @RolesAllowed({"ROLE_USER"})
    @ResponseBody
    public ResponseEntity<String> registerApplication(@RequestBody RegisterApplicationWrapper registerApplicationWrapper,
                                                      Authentication auth) {

        // check for given category
        if (registerApplicationWrapper.getSelectedCategory().isBlank()) {
            return ResponseEntity.badRequest().body("Please select a major!");
        }
        Subject s = subjectRepository.findByMajorSubject(registerApplicationWrapper.getSelectedCategory());
        if (s == null) {
            return ResponseEntity.badRequest().body("No such major found!");
        }

        // check for subjects
        if (registerApplicationWrapper.getSelectedSubjects().isEmpty()) {
            return ResponseEntity.badRequest().body("No subject entered!");
        }
        if (!s.getSubjects().containsAll(registerApplicationWrapper.getSelectedSubjects())) {
            return ResponseEntity.badRequest().body("Selected subjects does not belong to the selected major!");
        }

        // check for explain message
        if (registerApplicationWrapper.getExplainMsg().isBlank()) {
            return ResponseEntity.badRequest().body("Please write something about yourself.");
        }

        Subject fields = new Subject();
        fields.setMajorSubject(registerApplicationWrapper.getSelectedCategory());
        fields.getSubjects().addAll(registerApplicationWrapper.getSelectedSubjects());

        MentorshipRequest request = new MentorshipRequest();
        User currentUser = userRepository.findByUsernameOrGoogleId(auth.getName(), auth.getName());
        request.setMentor(currentUser);
        request.setStatus("waiting");
        request.setText(registerApplicationWrapper.getExplainMsg());
        request.setSelectedSubject(fields);
        request.setDate(new Date());
        mentorshipRequestRepository.save(request);

        return ResponseEntity.ok().build();
    }

}
