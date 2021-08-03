package com.spaceymonk.mentorhub.controller.wrapper;

import lombok.Data;

import java.util.List;

@Data
public class RegisterApplicationWrapper {
    private String selectedCategory;
    private List<String> selectedSubjects;
    private String explainMsg;
}
