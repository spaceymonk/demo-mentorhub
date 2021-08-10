package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.Mentorship;
import com.spaceymonk.mentorhub.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MentorshipRepository extends MongoRepository<Mentorship, String> {
    List<Mentorship> findByMentor(User mentor);
}