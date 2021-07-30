package com.spaceymonk.mentorhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @GetMapping("/")
    public String loginPage(Model model, HttpServletRequest request) {
        String errMsg = (String) request.getSession().getAttribute("error.message");
        request.getSession().removeAttribute("error.message");
        model.addAttribute("msg", errMsg);
        return "index";
    }

}
