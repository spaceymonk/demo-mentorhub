package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * The interface User repository.
 * Establishes connection between MongoDb and application.
 * Connects to user collection.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by username.
     *
     * @param username the username
     * @return the user
     */
    User findByUsername(String username);

    /**
     * Find user by google id.
     *
     * @param id Google id
     * @return the user
     */
    User findByGoogleId(String id);

    /**
     * Find user by username or google id.
     *
     * @param username the username
     * @param id       Google id
     * @return the user
     */
    User findByUsernameOrGoogleId(String username, String id);
}
