package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.Mentorship;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MentorshipRepository extends MongoRepository<Mentorship, String> {
    List<Mentorship> findByPhasesEndDateBetweenAndPhasesNotifiedAndCurrentPhaseIndexGreaterThan(Date start, Date end, Boolean notified, Integer index);
}