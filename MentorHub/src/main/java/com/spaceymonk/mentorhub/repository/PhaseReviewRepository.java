package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.PhaseReview;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhaseReviewRepository extends MongoRepository<PhaseReview, String> {
}
