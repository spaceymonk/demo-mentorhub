package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;


/**
 * Model class for mentorship request entities.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MentorshipRequest {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private Subject selectedSubject;
    private String text;
    @DBRef
    private User mentor;
    private String status;
    private String adminMsg;
    private Date date;

    /**
     * Instantiates a new Mentorship request.
     */
    public MentorshipRequest() {
    }
}
