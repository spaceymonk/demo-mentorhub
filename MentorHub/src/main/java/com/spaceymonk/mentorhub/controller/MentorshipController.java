package com.spaceymonk.mentorhub.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.security.RolesAllowed;

@Controller
@AllArgsConstructor
public class MentorshipController {

    @GetMapping(value = "/details/{id}")
    @RolesAllowed({"ROLE_USER"})
    public String displayMentorshipDetails(@PathVariable("id") String mentorshipId) {
        return "";
    }
}
