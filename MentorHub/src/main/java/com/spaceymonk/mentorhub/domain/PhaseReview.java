package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PhaseReview {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String text;
    private int rating;
}
