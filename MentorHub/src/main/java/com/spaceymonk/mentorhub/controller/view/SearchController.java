package com.spaceymonk.mentorhub.controller.view;

import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;
import java.util.List;


/**
 * The Search page controller.
 * Handles <code>/search</code> endpoint.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@Controller
public class SearchController {

    private UserRepository userRepository;
    private SubjectRepository subjectRepository;

    /**
     * Sets user repository.
     *
     * @param userRepository the user repository
     */
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Sets subject repository.
     *
     * @param subjectRepository the subject repository
     */
    @Autowired
    public void setSubjectRepository(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    /**
     * Renders search page.
     * It generates view for <code>ROLE_USER</code> role.
     *
     * @param model          the model
     * @param authentication the authentication
     * @return string that represents model and view
     */
    @GetMapping("/search")
    @RolesAllowed({"ROLE_USER"})
    public String renderSearchPage(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());
        model.addAttribute("currentUser", currentUser);
        List<Subject> categories = subjectRepository.findAll();
        model.addAttribute("categories", categories);
        return "features/search";
    }
}
