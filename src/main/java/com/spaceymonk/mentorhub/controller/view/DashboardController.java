package com.spaceymonk.mentorhub.controller.view;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;
import java.util.Comparator;
import java.util.List;


/**
 * The Dashboard page controller class.
 * Handles <code>/dashboard</code> endpoint.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@Controller
@AllArgsConstructor
public class DashboardController {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    /**
     * Renders dashboard page.
     * It generates view for both <code>ROLE_USER</code> and <code>ROLE_ADMIN</code> roles.
     *
     * @param model          the model
     * @param authentication the authentication
     * @return string that represents model and view
     */
    @GetMapping("/dashboard")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public String renderDashboardPage(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());
        model.addAttribute("currentUser", currentUser);

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            return "features/dashboard_user";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            List<MentorshipRequest> waitingRequests = mentorshipRequestRepository.findByStatus("waiting");
            waitingRequests.sort(Comparator.comparing(MentorshipRequest::getDate).reversed());
            model.addAttribute("waitingRequests", waitingRequests);
            return "features/dashboard_admin";
        }

        return "redirect:/error";
    }
}
