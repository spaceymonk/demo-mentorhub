package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


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
    Slice<MentorshipRequest> findByStatus(String status, Pageable pageable);

    /**
     * Find requests by mentor.
     *
     * @param user the user
     * @return the list
     */
    Slice<MentorshipRequest> findByMentor(User user, Pageable pageable);
}
