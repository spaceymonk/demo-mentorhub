package com.spaceymonk.mentorhub.controller.view;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;
import java.util.Comparator;
import java.util.List;

/**
 * Mentorship requests page controller.
 * Handles <code>/apply</code> endpoint.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@Controller
@AllArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    /**
     * Renders Application page.
     * It generates view for <code>ROLE_USER</code> role.
     *
     * @param model          the model
     * @param authentication the authentication
     * @return string that represents model and view
     */
    @GetMapping("/apply")
    @RolesAllowed({"ROLE_USER"})
    public String renderApplicationPage(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());
        model.addAttribute("currentUser", currentUser);
        List<Subject> categories = subjectRepository.findAll();
        model.addAttribute("categories", categories);
        List<MentorshipRequest> requests = mentorshipRequestRepository.findByMentor(currentUser);
        requests.sort(Comparator.comparing(MentorshipRequest::getDate).reversed());
        model.addAttribute("requests", requests);
        return "features/mentor-application";
    }
}
