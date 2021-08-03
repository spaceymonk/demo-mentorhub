package com.spaceymonk.mentorhub.controller;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class SearchController {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;


    @GetMapping("/search")
    public String searchPage(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());
        model.addAttribute("currentUser", currentUser);
        List<MentorshipRequest> mentorshipRequestList = mentorshipRequestRepository.findByStatus("accepted");
        model.addAttribute("mentorshipRequestList", mentorshipRequestList);
        return "features/search";
    }
}
