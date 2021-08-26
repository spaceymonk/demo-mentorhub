package com.spaceymonk.mentorhub.controller.view;

import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.security.RolesAllowed;


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
    public String renderDashboardPage(@RequestParam(defaultValue = "0", required = false) Integer pageNum,
                                      Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());
        model.addAttribute("currentUser", currentUser);

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            return "features/dashboard_user";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            pageNum = pageNum < 0 ? 0 : pageNum;
            Pageable page = PageRequest.of(pageNum, 3, Sort.by("date").descending());
            var waitingRequests = mentorshipRequestRepository.findByStatus("waiting", page);
            model.addAttribute("pageNum", pageNum);
            model.addAttribute("waitingRequests", waitingRequests);
            return "features/dashboard_admin";
        }

        return "redirect:/error";
    }
}
