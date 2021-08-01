package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MentorshipRequest {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private Subject selectedSubject;
    private String text;
    private User mentor;
    private String status;
    private Date date;

}
