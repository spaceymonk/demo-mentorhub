package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.features.EmailSender;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.Date;
import java.util.Optional;


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
public class ApiRequests {

    private UserRepository userRepository;
    private MentorshipRequestRepository mentorshipRequestRepository;
    private SubjectRepository subjectRepository;
    private EmailSender emailSender;

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
     * Sets mentorship request repository.
     *
     * @param mentorshipRequestRepository the mentorship request repository
     */
    @Autowired
    public void setMentorshipRequestRepository(MentorshipRequestRepository mentorshipRequestRepository) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
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
     * Sets email sender.
     *
     * @param emailSender the email sender
     */
    @Autowired
    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

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

        Optional<MentorshipRequest> mentorshipRequestOptional = mentorshipRequestRepository.findById(response.getId());
        if (mentorshipRequestOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MentorshipRequest mentorshipRequest = mentorshipRequestOptional.get();

        if (!mentorshipRequest.getStatus().equals("waiting")) {
            return ResponseEntity.badRequest().body("Already " + mentorshipRequest.getStatus() + "!");
        }

        mentorshipRequest.setStatus(response.getStatus());
        mentorshipRequest.setAdminMsg(response.getAdminMsg());

        mentorshipRequestRepository.save(mentorshipRequest);

        emailSender.send(mentorshipRequest.getMentor().getEmail(), "Mentorship Request Answered!",
                "<p>Hey, your mentorship request has been answered! Come and take a look!!</p>");

        return ResponseEntity.ok().build();
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

        return ResponseEntity.ok(mentorshipRequest.getId());
    }
}
