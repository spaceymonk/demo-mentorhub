package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;


/**
 * Model class for User entities.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 * @see com.spaceymonk.mentorhub.repository.UserRepository
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String googleId;
    private String email;
    private String username;
    private String password;
    private String actualName;
    private boolean enabled;
    private Set<Role> roles;
    @DBRef
    private Set<Mentorship> mentorshipSet = new HashSet<>();

    /**
     * Instantiates a new User.
     */
    public User() {
    }

    /**
     * Gets mentee sections.
     *
     * @return the mentee sections
     */
    public Set<Mentorship> getMenteeSections() {
        Set<Mentorship> set = new HashSet<>();
        for (Mentorship ship : mentorshipSet)
            if (ship.getMentee().equals(this))
                set.add(ship);
        return set;
    }

    /**
     * Gets mentor sections.
     *
     * @return the mentor sections
     */
    public Set<Mentorship> getMentorSections() {
        Set<Mentorship> set = new HashSet<>();
        for (Mentorship ship : mentorshipSet)
            if (ship.getMentor().equals(this))
                set.add(ship);
        return set;
    }
}
