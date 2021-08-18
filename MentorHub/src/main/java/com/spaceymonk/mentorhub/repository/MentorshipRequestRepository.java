package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * The interface Mentorship request repository.
 * Establishes connection between MongoDb and application.
 * Connects to mentorshipRequest collection.
 */
@Repository
public interface MentorshipRequestRepository extends MongoRepository<MentorshipRequest, String> {

    /**
     * Find requests by status.
     *
     * @param status the status
     * @return the list
     */
    List<MentorshipRequest> findByStatus(String status);

    /**
     * Find requests by mentor.
     *
     * @param user the user
     * @return the list
     */
    List<MentorshipRequest> findByMentor(User user);
}
