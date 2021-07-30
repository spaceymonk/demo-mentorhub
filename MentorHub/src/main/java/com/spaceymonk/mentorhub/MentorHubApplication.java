package com.spaceymonk.mentorhub;

import com.spaceymonk.mentorhub.repository.UserRepository;
import com.spaceymonk.mentorhub.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MentorHubApplication implements CommandLineRunner {

    private final UserRepository userRepository;

    public MentorHubApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(MentorHubApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.save(new User("Ralph", "Admin"));
        System.out.println(userRepository.findAll());
    }
}
