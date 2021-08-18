package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;


/**
 * Model class for subject entities.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Subject {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String majorSubject;
    private List<String> subjects = new ArrayList<>();

    /**
     * Instantiates a new Subject.
     */
    public Subject() {
    }
}
