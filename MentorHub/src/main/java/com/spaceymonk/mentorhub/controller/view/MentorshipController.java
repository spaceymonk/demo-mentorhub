package com.spaceymonk.mentorhub.controller.view;

import com.spaceymonk.mentorhub.domain.Mentorship;
import com.spaceymonk.mentorhub.repository.MentorshipRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class MentorshipController {

    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;


    private boolean getMentorshipData(String mentorshipId, Model model, Authentication authentication) {
        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            model.addAttribute("msg", "no such mentorship!");
            return true;
        }
        model.addAttribute("mentorship", mentorshipOptional.get());
        model.addAttribute("currentUser", userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName()));
        return false;
    }

    @GetMapping(value = "/details/{id}")
    @RolesAllowed({"ROLE_USER"})
    public String mentorshipPage(@PathVariable("id") String mentorshipId,
                                 Model model, Authentication authentication) {
        if (getMentorshipData(mentorshipId, model, authentication)) return "error";

        return "features/mentorship-details";
    }

    @GetMapping(value = "/plan/{id}")
    @RolesAllowed({"ROLE_USER"})
    public String planPage(@PathVariable("id") String mentorshipId,
                           Model model, Authentication authentication) {
        if (getMentorshipData(mentorshipId, model, authentication)) return "error";

        return "features/phase-planning";
    }

}
