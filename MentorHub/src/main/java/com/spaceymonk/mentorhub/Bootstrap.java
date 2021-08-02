package com.spaceymonk.mentorhub;

import com.spaceymonk.mentorhub.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final PhaseRepository phaseRepository;
    private final PhaseReviewRepository phaseReviewRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final SubjectRepository subjectRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Started in bootstrap...");

    }
}
