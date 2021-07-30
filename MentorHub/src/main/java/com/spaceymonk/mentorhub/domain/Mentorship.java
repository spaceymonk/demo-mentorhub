package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
public class Mentorship {

    @Id
    private String id;
    private User mentor;
    private User mentee;
    private Date beginDate;
    private List<Phase> phases = new LinkedList<>();
    private String status;
}
