package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorshipRequestRepository extends MongoRepository<MentorshipRequest, String> {
    List<MentorshipRequest> findByStatus(String status);

    List<MentorshipRequest> findByMentor(User user);
}
