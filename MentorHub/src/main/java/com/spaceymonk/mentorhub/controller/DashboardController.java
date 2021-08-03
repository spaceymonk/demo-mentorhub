package com.spaceymonk.mentorhub.controller;

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

@Controller
@AllArgsConstructor
public class DashboardController {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public String dashboardPage(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());
        model.addAttribute("currentUser", currentUser);

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            return "features/dashboard_user";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            model.addAttribute("waitingRequests", mentorshipRequestRepository.findByStatus("waiting"));
            return "features/dashboard_admin";
        }

        return "redirect:/error";
    }


}
