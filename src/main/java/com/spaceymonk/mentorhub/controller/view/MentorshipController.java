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


/**
 * The Mentorship page controller.
 * Handles <code>/details/{id}</code> and <code>/plan/{id}</code> endpoints
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@Controller
@AllArgsConstructor
public class MentorshipController {

    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;

    /**
     * Helper function that gets mentorship data from database.
     *
     * @param mentorshipId   mentorship id
     * @param model          the model
     * @param authentication the authentication
     * @throws RuntimeException no mentorship found with given id
     */
    private void getMentorshipData(String mentorshipId, Model model, Authentication authentication)
            throws RuntimeException {
        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            throw new RuntimeException("No mentorship with id " + mentorshipId);
        }
        model.addAttribute("mentorship", mentorshipOptional.get());
        model.addAttribute("currentUser", userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName()));
    }

    /**
     * Renders mentorship page for the given id.
     * It generates view for <code>ROLE_USER</code> role.
     *
     * @param mentorshipId   the mentorship id
     * @param model          the model
     * @param authentication the authentication
     * @return string that represents model and view
     */
    @GetMapping(value = "/details/{id}")
    @RolesAllowed({"ROLE_USER"})
    public String renderMentorshipPage(@PathVariable("id") String mentorshipId,
                                       Model model, Authentication authentication) {
        try {
            getMentorshipData(mentorshipId, model, authentication);
        } catch (RuntimeException e) {
            model.addAttribute("exception", e);
            return "errors/error";
        }
        return "features/mentorship-details";
    }

    /**
     * Renders phase planning page for the given id.
     * It generates view for <code>ROLE_USER</code> role.
     *
     * @param mentorshipId   the mentorship id
     * @param model          the model
     * @param authentication the authentication
     * @return string that represents model and view
     */
    @GetMapping(value = "/plan/{id}")
    @RolesAllowed({"ROLE_USER"})
    public String renderPlanningPage(@PathVariable("id") String mentorshipId,
                                     Model model, Authentication authentication) {
        try {
            getMentorshipData(mentorshipId, model, authentication);
        } catch (RuntimeException e) {
            model.addAttribute("exception", e);
            return "errors/error";
        }
        return "features/phase-planning";
    }
}
