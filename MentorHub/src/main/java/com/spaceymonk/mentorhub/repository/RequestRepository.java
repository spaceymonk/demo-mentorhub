package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.Request;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RequestRepository extends MongoRepository<Request, String> {
}
