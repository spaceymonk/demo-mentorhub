package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;


/**
 * Model class for role entities.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;

    /**
     * Instantiates a new Role.
     */
    public Role() {
    }

    /**
     * Instantiates a new Role with given role name.
     *
     * @param name the name
     */
    public Role(String name) {
        this.name = name;
    }
}
