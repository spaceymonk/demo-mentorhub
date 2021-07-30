package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.Phase;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhaseRepository extends MongoRepository<Phase, String> {
}
