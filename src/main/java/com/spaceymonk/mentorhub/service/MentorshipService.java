package com.spaceymonk.mentorhub.service;

import com.spaceymonk.mentorhub.domain.Mentorship;
import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.MentorshipRepository;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MentorshipService {

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public MentorshipRequest validateMentorship(String mentorshipRequestId, User currentUser) {
        // check db
        Optional<MentorshipRequest> mentorshipRequestOptional = mentorshipRequestRepository.findById(mentorshipRequestId);
        if (mentorshipRequestOptional.isEmpty()) {
            throw new RuntimeException("There is no such mentorship request!");
        }
        MentorshipRequest mentorshipRequest = mentorshipRequestOptional.get();
        User requestOwner = mentorshipRequest.getMentor();

        // check business logic
        if (requestOwner.equals(currentUser)) {
            throw new RuntimeException("You cannot be mentor of yourself!");
        }
        int remainder = 2;
        for (Mentorship mentorship : requestOwner.getMentorSections()) {
            if (mentorship.isNotCompleted())
                remainder -= 1;
            if (remainder <= 0)
                throw new RuntimeException("Mentor is full!!");
        }
        for (Mentorship mentorship : currentUser.getMenteeSections()) {
            if (mentorship.isNotCompleted() &&
                    mentorship.getMajorSubject().equals(mentorshipRequest.getSelectedSubject().getMajorSubject())) {
                throw new RuntimeException("You can only study with only ONE mentor under the same major!");
            }
        }
        for (Mentorship mentorship : currentUser.getMentorSections()) {
            if (mentorship.getMajorSubject().equals(mentorshipRequest.getSelectedSubject().getMajorSubject())) {
                throw new RuntimeException("You already teaching this major!");
            }
        }
        return mentorshipRequest;
    }

    public String saveMentorshipToDb(String mentorshipRequestId, User currentUser) {
        MentorshipRequest mentorshipRequest = validateMentorship(mentorshipRequestId, currentUser);
        User requestOwner = mentorshipRequest.getMentor();

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

        return mentorship.getId();
    }

}
