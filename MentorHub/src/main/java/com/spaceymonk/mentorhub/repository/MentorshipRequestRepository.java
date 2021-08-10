package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MentorshipRequestRepository extends MongoRepository<MentorshipRequest, String> {
    List<MentorshipRequest> findByStatus(String status);

}
