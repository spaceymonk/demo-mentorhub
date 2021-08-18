package com.spaceymonk.mentorhub.domain;

import lombok.Data;


/**
 * Model class for phase review entities.
 * This class stored as embedded document in Phase object.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 * @see Phase
 */
@Data
public class PhaseReview {

    private String text;
    private Integer rating;

    /**
     * Instantiates a new Phase review.
     */
    public PhaseReview() {
    }
}
