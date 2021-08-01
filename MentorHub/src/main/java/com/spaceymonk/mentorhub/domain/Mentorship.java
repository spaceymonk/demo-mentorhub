package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Mentorship {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private User mentor;
    private User mentee;
    private Date beginDate;
    private List<Phase> phases = new LinkedList<>();
    private String status;
    private String majorSubject;
}
