package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {
    Subject findByMajorSubject(String name);
}
