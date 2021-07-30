package com.spaceymonk.mentorhub.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Request {

    @Id
    private String id;
    private Subject selectedSubject;
    private String text;
    private User mentor;
    private String status;

}
