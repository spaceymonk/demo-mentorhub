package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.*;
import com.spaceymonk.mentorhub.repository.MentorshipRepository;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/mentorships")
@RestController
@AllArgsConstructor
public class ApiMentorship {

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> saveMentorship(String mentorshipRequestId,
                                                 Authentication authentication) {

        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());

        // check db
        Optional<MentorshipRequest> mentorshipRequestOptional = mentorshipRequestRepository.findById(mentorshipRequestId);
        if (mentorshipRequestOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("There is no such mentorship request!");
        }
        MentorshipRequest mentorshipRequest = mentorshipRequestOptional.get();
        User requestOwner = mentorshipRequest.getMentor();

        // check business logic
        if (requestOwner.equals(currentUser)) {
            return ResponseEntity.badRequest().body("You cannot be mentor of yourself!");
        }
        int remainder = 2;
        for(Mentorship mentorship : requestOwner.getMentorSections()) {
            if (mentorship.isNotCompleted())
                remainder -= 1;
            if (remainder <= 0)
                return ResponseEntity.badRequest().body("Mentor is full!!");
        }
        for (Mentorship mentorship : currentUser.getMenteeSections()) {
            if (mentorship.isNotCompleted() &&
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
        mentorship.setMajorSubject(mentorshipRequest.getSelectedSubject().getMajorSubject());
        mentorship.setCurrentPhaseIndex(-1);

        mentorshipRepository.save(mentorship);
        currentUser.getMentorshipSet().add(mentorship);
        requestOwner.getMentorshipSet().add(mentorship);
        userRepository.save(currentUser);
        userRepository.save(requestOwner);

        return ResponseEntity.ok(mentorship.getId());
    }

    @RequestMapping(value = "/{mentorshipId}/phases/", consumes = "application/json", method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> savePhase(@PathVariable("mentorshipId") String mentorshipId,
                                            @RequestBody Phase requestPhase) {

        if (requestPhase.getId() == null) {
            requestPhase.setId(new ObjectId().toHexString());
        }

        if (requestPhase.getName().isBlank()) {
            return ResponseEntity.badRequest().body("No name entered!");
        }

        if (requestPhase.getEndDate() == null) {
            return ResponseEntity.badRequest().body("You should set an end date!");
        }
        if (requestPhase.getEndDate().before(new Date())) {
            return ResponseEntity.badRequest().body("End date must be in future!");
        }

        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("No mentorship found");
        }
        Mentorship mentorship = mentorshipOptional.get();

        if (mentorship.isStarted()) {
            return ResponseEntity.badRequest().body("Mentorship already started!");
        }

        requestPhase.setNotified(false);

        int repoIndex = mentorship.getPhases().indexOf(requestPhase);
        if (repoIndex != -1) {
            mentorship.getPhases().set(repoIndex, requestPhase);
        } else {
            requestPhase.setId(new ObjectId().toHexString());
            mentorship.getPhases().add(requestPhase);
        }

        mentorship.getPhases().sort(Comparator.comparing(Phase::getEndDate));

        mentorshipRepository.save(mentorship);

        return ResponseEntity.ok(requestPhase.getId());
    }

    @RequestMapping(value = "/{mentorshipId}/nextPhase", method = RequestMethod.POST)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> nextPhase(@PathVariable("mentorshipId") String mentorshipId) {

        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("No mentorship found");
        }
        Mentorship mentorship = mentorshipOptional.get();

        if (mentorship.getPhases().isEmpty()) {
            return ResponseEntity.badRequest().body("You need to add phases first!");
        }

        if (mentorship.isCompleted()) {
            return ResponseEntity.badRequest().body("Already completed!");
        }

        if (mentorship.isNotStarted()) {
            mentorship.setBeginDate(new Date());
        }

        mentorship.setCurrentPhaseIndex(mentorship.getCurrentPhaseIndex() + 1);

        mentorshipRepository.save(mentorship);

        if (mentorship.isCompleted())
            return ResponseEntity.ok().build();

        return ResponseEntity.ok(mentorship.getPhases().get(mentorship.getCurrentPhaseIndex()).getId());
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

        return ResponseEntity.ok(phaseId);
    }

    @RequestMapping(value = "/{mentorshipId}/phases/{phaseId}/reviews/", consumes = "application/json", method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> savePhaseReview(@PathVariable("mentorshipId") String mentorshipId,
                                                  @PathVariable("phaseId") String phaseId,
                                                  @RequestBody PhaseReview requestPhaseReview,
                                                  Authentication authentication) {

        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());

        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("No mentorship found");
        }
        Mentorship mentorship = mentorshipOptional.get();

        int phaseIndex = -1;
        for (int i = 0; i < mentorship.getPhases().size(); ++i)
            if (mentorship.getPhases().get(i).getId().equals(phaseId))
                phaseIndex = i;

        if (phaseIndex < 0) {
            return ResponseEntity.badRequest().body("No phase found");
        }

        if (requestPhaseReview.getRating() == null
                || !(requestPhaseReview.getRating() >= 1 && requestPhaseReview.getRating() <= 5)) {
            return ResponseEntity.badRequest().body("Enter a valid rating!");
        }

        if (requestPhaseReview.getText() == null || requestPhaseReview.getText().isBlank()) {
            return ResponseEntity.badRequest().body("Enter a valid text!");
        }

        if (mentorship.getMentor().equals(currentUser))
            mentorship.getPhases().get(phaseIndex).setMentorReview(requestPhaseReview);
        else if (mentorship.getMentee().equals(currentUser))
            mentorship.getPhases().get(phaseIndex).setMenteeReview(requestPhaseReview);
        else
            return ResponseEntity.badRequest().body("You are not authorized!");

        mentorshipRepository.save(mentorship);

        return ResponseEntity.ok(phaseId);
    }

}
