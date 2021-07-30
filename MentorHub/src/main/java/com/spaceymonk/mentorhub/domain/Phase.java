package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Phase {

    @Id
    private String id;
    private PhaseReview mentorReview;
    private PhaseReview menteeReview;
    private String name;
    private Date endDate;
    private String status;


}
