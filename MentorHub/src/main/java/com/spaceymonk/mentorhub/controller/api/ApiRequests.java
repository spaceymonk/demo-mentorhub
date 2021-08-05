package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/requests")
@RestController
@AllArgsConstructor
public class ApiRequests {

    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final SubjectRepository subjectRepository;

    @RequestMapping(value = "/{id}/{answer}", method = RequestMethod.POST)
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<String> saveMentorshipRequestAnswer(@PathVariable("id") String requestId,
                                                              @PathVariable("answer") boolean answer) {

        Optional<MentorshipRequest> mentorshipRequestOptional = mentorshipRequestRepository.findById(requestId);
        if (mentorshipRequestOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MentorshipRequest mentorshipRequest = mentorshipRequestOptional.get();
        mentorshipRequest.setStatus((answer) ? "accepted" : "rejected");
        mentorshipRequestRepository.save(mentorshipRequest);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/", consumes = "application/json",method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> createMentorshipRequest(@RequestBody MentorshipRequest mentorshipRequest,
                                                          Authentication authentication) {

        // check for given category
        if (mentorshipRequest.getSelectedSubject().getMajorSubject().isBlank()) {
            return ResponseEntity.badRequest().body("Please select a major!");
        }
        Subject s = subjectRepository.findByMajorSubject(mentorshipRequest.getSelectedSubject().getMajorSubject());
        if (s == null) {
            return ResponseEntity.badRequest().body("No such major found!");
        }

        // check for subjects
        if (mentorshipRequest.getSelectedSubject().getSubjects().isEmpty()) {
            return ResponseEntity.badRequest().body("No subject entered!");
        }
        if (!s.getSubjects().containsAll(mentorshipRequest.getSelectedSubject().getSubjects())) {
            return ResponseEntity.badRequest().body("Selected subjects does not belong to the selected major!");
        }

        // check for explain message
        if (mentorshipRequest.getText().isBlank()) {
            return ResponseEntity.badRequest().body("Please write something about yourself.");
        }

        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());
        mentorshipRequest.setMentor(currentUser);
        mentorshipRequest.setStatus("waiting");
        mentorshipRequest.setDate(new Date());
        mentorshipRequestRepository.save(mentorshipRequest);

        return ResponseEntity.ok().build();
    }
}
