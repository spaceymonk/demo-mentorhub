package com.spaceymonk.mentorhub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

    @GetMapping("/error")
    @ResponseBody
    public String errorPage() {
        return "Something went wrong!";
    }

    @GetMapping("/403")
    public String accessDeniedPage(Model model, Authentication authentication) {
        model.addAttribute("auth", authentication);
        return "errors/403";
    }
}
