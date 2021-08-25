package com.spaceymonk.mentorhub.service;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.features.EmailSender;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final SubjectRepository subjectRepository;
    private final EmailSender emailSender;

    public MentorshipRequest validateAdminReview(MentorshipRequest response) {
        Optional<MentorshipRequest> mentorshipRequestOptional = mentorshipRequestRepository.findById(response.getId());
        if (mentorshipRequestOptional.isEmpty()) {
            throw new RuntimeException("No such mentorship!");
        }
        MentorshipRequest mentorshipRequest = mentorshipRequestOptional.get();
        if (!mentorshipRequest.getStatus().equals("waiting")) {
            throw new RuntimeException("Already " + mentorshipRequest.getStatus() + "!");
        }

        return mentorshipRequest;
    }

    public String saveAdminReviewToDb(MentorshipRequest response) {
        MentorshipRequest mentorshipRequest = validateAdminReview(response);

        mentorshipRequest.setStatus(response.getStatus());
        mentorshipRequest.setAdminMsg(response.getAdminMsg());

        mentorshipRequestRepository.save(mentorshipRequest);

        emailSender.send(mentorshipRequest.getMentor().getEmail(), "Mentorship Request Answered!",
                "<p>Hey, your mentorship request has been answered! Come and take a look!!</p>");

        return mentorshipRequest.getId();
    }

    public void validateMentorshipRequest(MentorshipRequest mentorshipRequest) {
        // check for given category
        if (mentorshipRequest.getSelectedSubject().getMajorSubject().isBlank()) {
            throw new RuntimeException("Please select a major!");
        }
        Subject s = subjectRepository.findByMajorSubject(mentorshipRequest.getSelectedSubject().getMajorSubject());
        if (s == null) {
            throw new RuntimeException("No such major found!");
        }

        // check for subjects
        if (mentorshipRequest.getSelectedSubject().getSubjects().isEmpty()) {
            throw new RuntimeException("No subject entered!");
        }
        if (!s.getSubjects().containsAll(mentorshipRequest.getSelectedSubject().getSubjects())) {
            throw new RuntimeException("Selected subjects does not belong to the selected major!");
        }

        // check for explain message
        if (mentorshipRequest.getText().isBlank()) {
            throw new RuntimeException("Please write something about yourself.");
        }
    }

    public String saveMentorshipRequestToDb(User currentUser, MentorshipRequest mentorshipRequest) {
        validateMentorshipRequest(mentorshipRequest);

        mentorshipRequest.setMentor(currentUser);
        mentorshipRequest.setStatus("waiting");
        mentorshipRequest.setDate(new Date());
        mentorshipRequestRepository.save(mentorshipRequest);

        return mentorshipRequest.getId();
    }

}
