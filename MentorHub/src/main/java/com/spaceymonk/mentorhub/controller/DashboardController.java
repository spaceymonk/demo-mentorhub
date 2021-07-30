package com.spaceymonk.mentorhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {
    @GetMapping("/dashboard")
    public String dashboardPage(Model model, Principal principal) {
        model.addAttribute("name", principal.getName());
        return "features/dashboard";
    }
}
