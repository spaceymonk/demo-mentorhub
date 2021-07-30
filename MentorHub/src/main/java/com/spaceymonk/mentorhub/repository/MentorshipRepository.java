package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.Mentorship;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MentorshipRepository extends MongoRepository<Mentorship, String> {
}