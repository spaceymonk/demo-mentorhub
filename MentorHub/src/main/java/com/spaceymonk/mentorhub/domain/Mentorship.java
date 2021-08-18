package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * Model class for mentorship entities.
 * This class stored to database as separate collection.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 * @see com.spaceymonk.mentorhub.repository.MentorshipRepository
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Mentorship {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    @DBRef
    private User mentor;
    @DBRef
    private User mentee;
    private Date beginDate;
    private List<Phase> phases = new LinkedList<>();
    private int currentPhaseIndex;
    private String majorSubject;


    /**
     * Instantiates a new Mentorship.
     */
    public Mentorship() {
    }

    /**
     * Checks if this mentorship has completed
     *
     * @return true if completed
     */
    public boolean isCompleted() {
        return phases.size() == currentPhaseIndex;
    }

    /**
     * Checks if this mentorship has not yet completed
     *
     * @return false if completed
     */
    public boolean isNotCompleted() {
        return phases.size() != currentPhaseIndex;
    }

    /**
     * Checks if this mentorship has not yet started
     *
     * @return false if started
     */
    public boolean isNotStarted() {
        return currentPhaseIndex < 0;
    }

    /**
     * Checks if this mentorship has started
     *
     * @return true if started
     */
    public boolean isStarted() {
        return currentPhaseIndex >= 0;
    }

    /**
     * Gets status of this mentorship.
     *
     * @return the status
     */
    public String getStatus() {
        if (isCompleted()) return "completed";
        if (isNotStarted()) return "unbegun";
        return phases.get(currentPhaseIndex).getName();
    }
}
