package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    @Id
    private String id;
    private String name;
    private String role;
    private Set<Mentorship> mentorshipSet = new HashSet<>();

    public User(String name, String role) {
        this.name = name;
        this.role = role;
    }

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
