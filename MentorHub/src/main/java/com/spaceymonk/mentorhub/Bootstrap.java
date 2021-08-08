package com.spaceymonk.mentorhub;

import com.spaceymonk.mentorhub.domain.Role;
import com.spaceymonk.mentorhub.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final SubjectRepository subjectRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Started in bootstrap...");

        // setup roles if they don't exist
        String[] roles = {"ROLE_USER", "ROLE_ADMIN"};
        for (String role : roles)
            if (roleRepository.findByName(role) == null)
                roleRepository.save(new Role(role));

    }
}
