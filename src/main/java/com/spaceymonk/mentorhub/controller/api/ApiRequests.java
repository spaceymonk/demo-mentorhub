package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.UserRepository;
import com.spaceymonk.mentorhub.service.MentorshipRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;


/**
 * API class for Mentorship Request related operations.
 * Handles <code>/api/requests/**</code> endpoint.
 * <br/>
 * This API class does below operations:
 * <ul>
 *     <li>Create new mentorship request</li>
 *     <li>Update mentorship request's status</li>
 * </ul>
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@RequestMapping("/api/requests")
@RestController
@AllArgsConstructor
public class ApiRequests {

    private final UserRepository userRepository;
    private final MentorshipRequestService mentorshipRequestService;

    /**
     * Update mentorship request's status. <br>
     * This function changes the status of a request only if this request is present and has
     * status of <code>waiting</code>. Otherwise, an error text returns. Function only checks
     * id, admin message and status fields of the request.
     *
     * @param response MentorshipRequest object with explained fields.
     * @return nothing if successful, error text otherwise.
     */
    @RequestMapping(value = "/", consumes = "application/json", method = RequestMethod.POST)
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<String> saveMentorshipRequest(@RequestBody MentorshipRequest response) {
        try {
            String id = mentorshipRequestService.saveAdminReviewToDb(response);
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Create mentorship request entity. <br>
     * Does not check for id field, always new entity will be created.
     *
     * @param mentorshipRequest the mentorship request object
     * @param authentication    the authentication - autowired by Spring framework
     * @return if successful generated request id, error text otherwise.
     */
    @RequestMapping(value = "/", consumes = "application/json", method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<String> saveMentorshipRequest(@RequestBody MentorshipRequest mentorshipRequest,
                                                        Authentication authentication) {
        User currentUser = userRepository.findByUsernameOrGoogleId(authentication.getName(), authentication.getName());
        try {
            String id = mentorshipRequestService.saveMentorshipRequestToDb(currentUser, mentorshipRequest);
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
