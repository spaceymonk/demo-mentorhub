package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PhaseReview {
    private String text;
    private int rating;
}
