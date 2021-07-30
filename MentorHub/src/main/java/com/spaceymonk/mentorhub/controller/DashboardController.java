package com.spaceymonk.mentorhub.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {
    @GetMapping("/dashboard")
    public String dashboardPage(Model model, Principal principal) {
        model.addAttribute("name", principal.getName());
        return "dashboard";
    }
}
