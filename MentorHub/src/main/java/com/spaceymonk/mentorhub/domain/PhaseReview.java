package com.spaceymonk.mentorhub.domain;

import lombok.Data;

/**
 * Model class for phase review entities.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
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
