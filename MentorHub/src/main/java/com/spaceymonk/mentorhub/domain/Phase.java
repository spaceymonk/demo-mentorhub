package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.Date;


/**
 * Model class for phase entities.
 * <br>
 * This class stored as embedded document in mentorship collection in database.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 * @see Mentorship
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Phase {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private PhaseReview mentorReview;
    private PhaseReview menteeReview;
    private String name;
    private Date endDate;
    private Boolean notified;

    /**
     * Instantiates a new Phase.
     */
    public Phase() {
    }
}
