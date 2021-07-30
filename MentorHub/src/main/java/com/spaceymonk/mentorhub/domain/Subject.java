package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Subject {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String majorSubject;
    private Set<String> subjects = new HashSet<>();


    public Subject(String majorSubject, Set<String> subjects) {
        this.majorSubject = majorSubject;
        this.subjects = subjects;
    }
}
