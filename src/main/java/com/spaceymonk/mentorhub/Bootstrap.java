package com.spaceymonk.mentorhub;

import com.spaceymonk.mentorhub.domain.Role;
import com.spaceymonk.mentorhub.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * Run commands after server startup by initializing environment.
 * This class is mainly used for demonstration purposes.
 *
 * @author spaceymonk
 * @version 1.0, 08/17/21
 * @see CommandLineRunner
 */
@Component
public class Bootstrap implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Autowired
    public Bootstrap(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Run commands after server startup.
     *
     * @param args Command line arguments
     */
    @Override
    public void run(String... args) {
        System.out.println("Started in bootstrap...");

        // setup roles if they don't exist
        String[] roles = {"ROLE_USER", "ROLE_ADMIN"};
        for (String role : roles)
            if (roleRepository.findByName(role) == null)
                roleRepository.save(new Role(role));
    }
}
