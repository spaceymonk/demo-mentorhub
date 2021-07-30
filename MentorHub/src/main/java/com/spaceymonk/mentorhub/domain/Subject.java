package com.spaceymonk.mentorhub.domain;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Subject {

    private String majorSubject;
    private Set<String> subjects = new HashSet<>();

}
