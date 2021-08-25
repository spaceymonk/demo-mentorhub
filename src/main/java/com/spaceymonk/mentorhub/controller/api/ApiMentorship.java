package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.Phase;
import com.spaceymonk.mentorhub.domain.PhaseReview;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.UserRepository;
import com.spaceymonk.mentorhub.service.MentorshipService;
import com.spaceymonk.mentorhub.service.PhaseService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;


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
@AllArgsConstructor
public class ApiMentorship {

    private final MentorshipService mentorshipService;
    private final PhaseService phaseService;
    private final UserRepository userRepository;

    /**
     * Create a new mentorship.
     *
     * @param mentorshipRequestId the mentorship request id
     * @param authentication      the authentication -- Autowired by Spring framework
     * @return if successful generated mentorship id, error text otherwise.
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> saveMentorship(String mentorshipRequestId, Authentication authentication) {
        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());
        try {
            String id = mentorshipService.saveMentorshipToDb(mentorshipRequestId, currentUser);
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
        try {
            String id = phaseService.savePhaseToDb(mentorshipId, requestPhase);
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
        try {
            String id = phaseService.activateNextPhase(mentorshipId);
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
        try {
            String id = phaseService.removePhaseFromDb(mentorshipId, phaseId);
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
        try {
            String id = phaseService.saveReviewToDb(currentUser, mentorshipId, phaseId, requestPhaseReview);
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
