package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

@Data
public class Subject {

    @Id
    private String id;
    private String majorSubject;
    private Set<String> subjects = new HashSet<>();

}
