package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.*;
import com.spaceymonk.mentorhub.repository.MentorshipRepository;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;


/**
 * API class for Mentorship related operations.
 * Handles <code>/api/mentorships/**</code> endpoint.
 * <br/>
 * This API class does below operations:
 * <ul>
 *     <li>Create new mentorship</li>
 *     <li>Create new phaseReview</li>
 *     <li>Create new phase</li>
 *     <li>Activate next phase</li>
 *     <li>Remove phase</li>
 * </ul>
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@RequestMapping("/api/mentorships")
@RestController
public class ApiMentorship {

    private UserRepository userRepository;
    private MentorshipRepository mentorshipRepository;
    private MentorshipRequestRepository mentorshipRequestRepository;

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
     * Sets mentorship repository.
     *
     * @param mentorshipRepository the mentorship repository
     */
    @Autowired
    public void setMentorshipRepository(MentorshipRepository mentorshipRepository) {
        this.mentorshipRepository = mentorshipRepository;
    }

    /**
     * Sets mentorship request repository.
     *
     * @param mentorshipRequestRepository the mentorship request repository
     */
    @Autowired
    public void setMentorshipRequestRepository(MentorshipRequestRepository mentorshipRequestRepository) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
    }

    /**
     * Create a new mentorship.
     *
     * @param mentorshipRequestId the mentorship request id
     * @param authentication      the authentication -- Autowired by Spring framework
     * @return if successful generated mentorship id, error text otherwise.
     */
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
        for (Mentorship mentorship : requestOwner.getMentorSections()) {
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

        // save mentorship
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

    /**
     * Create a new phase or overwrite the existing one.
     * <br>
     * If id field of phase parameter left <code>null</code> then create a new
     * phase object in the system.
     *
     * @param mentorshipId the mentorship id
     * @param requestPhase a Phase object
     * @return if successful phase id, error text otherwise.
     */
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

    /**
     * Activate next phase for the system
     *
     * @param mentorshipId the mentorship id
     * @return - empty body if all phases completed <br>
     * - error text if an error occurred <br>
     * - index of the next phase
     */
    @RequestMapping(value = "/{mentorshipId}/nextPhase", method = RequestMethod.POST)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> nextPhase(@PathVariable("mentorshipId") String mentorshipId) {

        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("No mentorship found");
        }
        Mentorship mentorship = mentorshipOptional.get();

        if (mentorship.isCompleted()) {
            return ResponseEntity.badRequest().body("Already completed!");
        }

        if (mentorship.getPhases().isEmpty()) {
            return ResponseEntity.badRequest().body("You need to add phases first!");
        }

        if (mentorship.getPhases().get(0).getEndDate().before(new Date())) {
            return ResponseEntity.badRequest().body("Phase's end date is in past! Update your end dates.");
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

    /**
     * Remove phase from mentorship entity.
     *
     * @param mentorshipId the mentorship id
     * @param phaseId      the phase id
     * @return removed phase id
     */
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

    /**
     * Create a phase review for the given phase of this mentorship
     *
     * @param mentorshipId       the mentorship id
     * @param phaseId            the phase id
     * @param requestPhaseReview a PhaseReview object
     * @param authentication     the authentication - Autowired by Spring framework
     * @return phase id if successful, error text otherwise.
     */
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
