package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;

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

    public Set<Mentorship> getMenteeSections() {
        Set<Mentorship> set = new HashSet<>();
        for (Mentorship ship : mentorshipSet)
            if (ship.getMentee().equals(this))
                set.add(ship);
        return set;
    }

    public Set<Mentorship> getMentorSections() {
        Set<Mentorship> set = new HashSet<>();
        for (Mentorship ship : mentorshipSet)
            if (ship.getMentor().equals(this))
                set.add(ship);
        return set;
    }
}
