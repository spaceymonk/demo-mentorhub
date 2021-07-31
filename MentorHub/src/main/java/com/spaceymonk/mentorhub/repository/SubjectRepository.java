package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubjectRepository extends MongoRepository<Subject, String> {
    Subject findByMajorSubject(String name);
}
