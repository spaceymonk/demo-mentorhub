package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.Date;

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


}
