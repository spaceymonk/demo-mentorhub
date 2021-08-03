package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.Mentorship;
import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@RequestMapping("/api/mentorship")
@RestController
@AllArgsConstructor
public class ApiMentorship {

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final SubjectRepository subjectRepository;
    private final RoleRepository roleRepository;

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> createMentorship(String mentorshipRequestId,
                                                   Authentication authentication) {

        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());

        // check db
        Optional<MentorshipRequest> mentorshipRequestOptional = mentorshipRequestRepository.findById(mentorshipRequestId);
        if (mentorshipRequestOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("There is no such mentorship!");
        }
        MentorshipRequest mentorshipRequest = mentorshipRequestOptional.get();
        User requestOwner = mentorshipRequest.getMentor();

        // check business logic
        if (requestOwner.equals(currentUser)) {
            return ResponseEntity.badRequest().body("You cannot be mentor of yourself!");
        }
        for (Mentorship mentorship : currentUser.getMenteeSections()) {
            if (!mentorship.getStatus().equals("finished") &&
                    mentorship.getMajorSubject().equals(mentorshipRequest.getSelectedSubject().getMajorSubject())) {
                return ResponseEntity.badRequest().body("You can only study with only ONE mentor under the same major!");
            }
        }
        for (Mentorship mentorship : currentUser.getMentorSections()) {
            if (mentorship.getMajorSubject().equals(mentorshipRequest.getSelectedSubject().getMajorSubject())) {
                return ResponseEntity.badRequest().body("You already teaching this major!");
            }
        }

        Mentorship mentorship = new Mentorship();
        mentorship.setMentor(requestOwner);
        mentorship.setMentee(currentUser);
        mentorship.setStatus("unbegun");
        mentorship.setMajorSubject(mentorshipRequest.getSelectedSubject().getMajorSubject());

        mentorshipRepository.save(mentorship);
        currentUser.getMentorshipSet().add(mentorship);
        requestOwner.getMentorshipSet().add(mentorship);
        userRepository.save(currentUser);
        userRepository.save(requestOwner);

        return ResponseEntity.ok().build();
    }

}
