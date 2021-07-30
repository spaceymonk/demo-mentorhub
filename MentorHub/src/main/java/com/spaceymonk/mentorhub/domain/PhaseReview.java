package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class PhaseReview {
    @Id
    private String id;
    private String text;
    private Integer rating;
}
