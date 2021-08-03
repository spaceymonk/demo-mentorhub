package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.controller.wrapper.RegisterApplicationWrapper;
import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Date;
import java.util.Optional;

@RequestMapping("/api/requests")
@RestController
@AllArgsConstructor
public class ApiRequests {

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final PhaseRepository phaseRepository;
    private final PhaseReviewRepository phaseReviewRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final SubjectRepository subjectRepository;
    private final RoleRepository roleRepository;

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

    @RequestMapping(value = "/", consumes = "application/json", method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> createMentorshipRequest(@RequestBody RegisterApplicationWrapper registerApplicationWrapper,
                                                          Authentication auth) {

        // check for given category
        if (registerApplicationWrapper.getSelectedCategory().isBlank()) {
            return ResponseEntity.badRequest().body("Please select a major!");
        }
        Subject s = subjectRepository.findByMajorSubject(registerApplicationWrapper.getSelectedCategory());
        if (s == null) {
            return ResponseEntity.badRequest().body("No such major found!");
        }

        // check for subjects
        if (registerApplicationWrapper.getSelectedSubjects().isEmpty()) {
            return ResponseEntity.badRequest().body("No subject entered!");
        }
        if (!s.getSubjects().containsAll(registerApplicationWrapper.getSelectedSubjects())) {
            return ResponseEntity.badRequest().body("Selected subjects does not belong to the selected major!");
        }

        // check for explain message
        if (registerApplicationWrapper.getExplainMsg().isBlank()) {
            return ResponseEntity.badRequest().body("Please write something about yourself.");
        }

        Subject fields = new Subject();
        fields.setMajorSubject(registerApplicationWrapper.getSelectedCategory());
        fields.getSubjects().addAll(registerApplicationWrapper.getSelectedSubjects());

        MentorshipRequest request = new MentorshipRequest();
        User currentUser = userRepository.findByUsernameOrGoogleId(auth.getName(), auth.getName());
        request.setMentor(currentUser);
        request.setStatus("waiting");
        request.setText(registerApplicationWrapper.getExplainMsg());
        request.setSelectedSubject(fields);
        request.setDate(new Date());
        mentorshipRequestRepository.save(request);

        return ResponseEntity.ok().build();
    }
}
