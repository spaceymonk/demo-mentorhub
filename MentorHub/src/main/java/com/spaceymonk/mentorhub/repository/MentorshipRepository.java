package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.Mentorship;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


/**
 * The interface Mentorship repository.
 * Establishes connection between MongoDb and application.
 * Connects to mentorship collection.
 */
@Repository
public interface MentorshipRepository extends MongoRepository<Mentorship, String> {

    /**
     * Find mentorships by phases' end date between given dates, notified field and current phase index greater than given index.
     *
     * @param start    the start date
     * @param end      the end date
     * @param notified the notified field
     * @param index    the index of the current phase
     * @return the list of mentorships that matches the criteria
     */
    List<Mentorship> findByPhasesEndDateBetweenAndPhasesNotifiedAndCurrentPhaseIndexGreaterThan(Date start, Date end, Boolean notified, Integer index);
}