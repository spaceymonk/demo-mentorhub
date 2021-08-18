package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * The interface Subject repository.
 * Establishes connection between MongoDb and application.
 * Connects to subject collection.
 */
@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {

    /**
     * Find subject by major subject.
     *
     * @param name the major name of the subject
     * @return the subject
     */
    Subject findByMajorSubject(String name);
}
