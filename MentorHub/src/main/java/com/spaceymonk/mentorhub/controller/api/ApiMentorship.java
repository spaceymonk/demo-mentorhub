package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.Mentorship;
import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.Phase;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.*;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@RequestMapping("/api/mentorships")
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

    @RequestMapping(value = "/{mentorshipId}/phases", consumes = "application/json", method = RequestMethod.POST)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> savePhase(@PathVariable("mentorshipId") String mentorshipId,
                                            @RequestBody Phase requestPhase) {

        if (requestPhase.getId() == null) {
            requestPhase.setId(new ObjectId().toHexString());
        }

        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("No mentorship found");
        }
        Mentorship mentorship = mentorshipOptional.get();

        int repoIndex = mentorship.getPhases().indexOf(requestPhase);
        if (repoIndex != -1) {
            mentorship.getPhases().get(repoIndex).setEndDate(requestPhase.getEndDate());
            mentorship.getPhases().get(repoIndex).setName(requestPhase.getName());
        } else {
            mentorship.getPhases().add(requestPhase);
        }

        mentorshipRepository.save(mentorship);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{mentorshipId}/phases/{phaseId}", method = RequestMethod.DELETE)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> deletePhase(@PathVariable("mentorshipId") String mentorshipId,
                                              @PathVariable("phaseId") String phaseId) {

        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("No mentorship found");
        }
        Mentorship mentorship = mentorshipOptional.get();

        mentorship.getPhases().removeIf(phase -> phaseId.equals(phase.getId()));

        mentorshipRepository.save(mentorship);

        return ResponseEntity.ok().build();
    }

}
