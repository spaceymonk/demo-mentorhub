package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Mentorship {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    @DBRef
    private User mentor;
    @DBRef
    private User mentee;
    private Date beginDate;
    private List<Phase> phases = new LinkedList<>();
    private String status;
    private String majorSubject;
}
