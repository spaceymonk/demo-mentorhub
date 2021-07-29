package com.spaceymonk.mentorhub.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/dashboard")
    public String dashboardPage(Model model, @AuthenticationPrincipal OAuth2User user) {
        model.addAllAttributes(user.getAttributes());
        return "dashboard";
    }

}
