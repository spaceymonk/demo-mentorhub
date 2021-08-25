package com.spaceymonk.mentorhub.service;

import com.spaceymonk.mentorhub.domain.Mentorship;
import com.spaceymonk.mentorhub.domain.Phase;
import com.spaceymonk.mentorhub.domain.PhaseReview;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.MentorshipRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PhaseService {

    private final MentorshipRepository mentorshipRepository;

    public Mentorship validatePhaseCreation(String mentorshipId, Phase requestPhase) {
        if (requestPhase.getName().isBlank()) {
            throw new RuntimeException("No name entered!");
        }

        if (requestPhase.getEndDate() == null) {
            throw new RuntimeException("You should set an end date!");
        }
        if (requestPhase.getEndDate().before(new Date())) {
            throw new RuntimeException("End date must be in future!");
        }

        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            throw new RuntimeException("No mentorship found");
        }

        Mentorship mentorship = mentorshipOptional.get();

        if (mentorship.isStarted()) {
            throw new RuntimeException("Mentorship already started!");
        }

        return mentorship;
    }

    public String savePhaseToDb(String mentorshipId, Phase requestPhase) {
        if (requestPhase.getId() == null) {
            requestPhase.setId(new ObjectId().toHexString());
        }

        Mentorship mentorship = validatePhaseCreation(mentorshipId, requestPhase);

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

        return requestPhase.getId();
    }

    public Mentorship validateNextPhaseActivation(String mentorshipId) {
        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            throw new RuntimeException("No mentorship found");
        }
        Mentorship mentorship = mentorshipOptional.get();

        if (mentorship.isCompleted()) {
            throw new RuntimeException("Already completed!");
        }

        if (mentorship.getPhases().isEmpty()) {
            throw new RuntimeException("You need to add phases first!");
        }

        if (mentorship.getPhases().get(0).getEndDate().before(new Date())) {
            throw new RuntimeException("Phase's end date is in past! Update your end dates.");
        }

        return mentorship;
    }

    public String activateNextPhase(String mentorshipId) {
        Mentorship mentorship = validateNextPhaseActivation(mentorshipId);

        if (mentorship.isNotStarted()) {
            mentorship.setBeginDate(new Date());
        }

        mentorship.setCurrentPhaseIndex(mentorship.getCurrentPhaseIndex() + 1);

        mentorshipRepository.save(mentorship);

        if (mentorship.isCompleted())
            return null;

        return mentorship.getPhases().get(mentorship.getCurrentPhaseIndex()).getId();
    }

    public String removePhaseFromDb(String mentorshipId, String phaseId) {
        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            throw new RuntimeException("No mentorship found");
        }
        Mentorship mentorship = mentorshipOptional.get();

        mentorship.getPhases().removeIf(phase -> phaseId.equals(phase.getId()));

        mentorshipRepository.save(mentorship);

        return phaseId;
    }

    private int indexOfPhase(Mentorship mentorship, String phaseId) {
        int phaseIndex = -1;
        for (int i = 0; i < mentorship.getPhases().size(); ++i)
            if (mentorship.getPhases().get(i).getId().equals(phaseId))
                phaseIndex = i;
        return phaseIndex;
    }

    public Mentorship validateReview(User currentUser, String mentorshipId, String phaseId, PhaseReview requestPhaseReview) {
        Optional<Mentorship> mentorshipOptional = mentorshipRepository.findById(mentorshipId);
        if (mentorshipOptional.isEmpty()) {
            throw new RuntimeException("No mentorship found");
        }
        Mentorship mentorship = mentorshipOptional.get();

        if (indexOfPhase(mentorship, phaseId) < 0) {
            throw new RuntimeException("No phase found");
        }

        if (requestPhaseReview.getRating() == null
                || !(requestPhaseReview.getRating() >= 1 && requestPhaseReview.getRating() <= 5)) {
            throw new RuntimeException("Enter a valid rating!");
        }

        if (requestPhaseReview.getText() == null || requestPhaseReview.getText().isBlank()) {
            throw new RuntimeException("Enter a valid text!");
        }

        if (!mentorship.getMentor().equals(currentUser) && !mentorship.getMentee().equals(currentUser))
            throw new RuntimeException("You are not authorized!");

        return mentorship;
    }

    public String saveReviewToDb(User currentUser, String mentorshipId, String phaseId, PhaseReview requestPhaseReview) {
        Mentorship mentorship = validateReview(currentUser, mentorshipId, phaseId, requestPhaseReview);
        int phaseIndex = indexOfPhase(mentorship, phaseId);

        if (mentorship.getMentor().equals(currentUser))
            mentorship.getPhases().get(phaseIndex).setMentorReview(requestPhaseReview);
        else
            mentorship.getPhases().get(phaseIndex).setMenteeReview(requestPhaseReview);

        mentorshipRepository.save(mentorship);

        return phaseId;
    }

}
