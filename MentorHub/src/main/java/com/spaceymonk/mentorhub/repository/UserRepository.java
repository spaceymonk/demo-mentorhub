package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);

    User findByGoogleId(String id);

    User findByUsernameOrGoogleId(String username, String id);
}
