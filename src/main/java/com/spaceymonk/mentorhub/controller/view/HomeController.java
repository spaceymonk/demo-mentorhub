package com.spaceymonk.mentorhub.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * The Home page controller class.
 * Home page used as login page in the application context.
 * Handles <code>/</code> endpoint
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@Controller
public class HomeController {

    /**
     * Renders login page.
     * No roles required
     *
     * @param model   the model
     * @param request the request
     * @return string that represents model and view
     */
    @GetMapping("/")
    public String renderLoginPage(Model model, HttpServletRequest request) {
        String errMsg = (String) request.getSession().getAttribute("error.message");
        request.getSession().removeAttribute("error.message");
        model.addAttribute("errorTxt", errMsg);
        return "index";
    }
}
