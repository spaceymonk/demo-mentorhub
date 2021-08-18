package com.spaceymonk.mentorhub.repository;

import com.spaceymonk.mentorhub.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * The interface Role repository.
 * Establishes connection between MongoDb and application.
 * Connects to role collection.
 */
@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    /**
     * Find role by name.
     *
     * @param name the name of the role
     * @return the role
     */
    Role findByName(String name);
}
