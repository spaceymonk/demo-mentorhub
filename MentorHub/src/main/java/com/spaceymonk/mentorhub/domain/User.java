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
    private Set<String> mentees = new HashSet<>();
    private Set<String> mentors = new HashSet<>();

    public User(String name, String role) {
        this.name = name;
        this.role = role;
    }
}
