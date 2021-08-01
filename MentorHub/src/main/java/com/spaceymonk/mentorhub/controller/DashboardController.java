package com.spaceymonk.mentorhub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public String dashboardPage(Model model, Authentication authentication) {
        System.out.println(authentication);
        model.addAttribute("auth", authentication);

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            return "features/dashboard_user";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "features/dashboard_admin";
        }

        return "redirect:/error";
    }
}
