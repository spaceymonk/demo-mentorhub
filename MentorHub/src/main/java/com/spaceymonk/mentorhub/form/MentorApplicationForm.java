package com.spaceymonk.mentorhub.form;

import com.spaceymonk.mentorhub.domain.Subject;
import lombok.Data;

import java.util.List;

@Data
public class MentorApplicationForm {


    private String category;
    private List<String> subjects;
    private String text;
}
