package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;


/**
 * Model class for mentorship request entities.
 * This class stored to database as separate collection.
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 * @see com.spaceymonk.mentorhub.repository.MentorshipRequestRepository
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
