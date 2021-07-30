package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByName(String name);
}
