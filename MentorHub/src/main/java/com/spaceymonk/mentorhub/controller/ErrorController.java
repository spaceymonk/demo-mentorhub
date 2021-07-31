package com.spaceymonk.mentorhub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {
    @GetMapping("/error")
    public String errorPage(Model model, HttpServletRequest request) {
        String errMsg = (String) request.getSession().getAttribute("error.message");
        request.getSession().removeAttribute("error.message");
        model.addAttribute("msg", errMsg);
        return "error";
    }

    @GetMapping("/403")
    public String accessDeniedPage(Model model, Authentication authentication) {
        return "errors/403";
    }
}
