package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MentorshipRequestRepository extends MongoRepository<MentorshipRequest, String> {
}
