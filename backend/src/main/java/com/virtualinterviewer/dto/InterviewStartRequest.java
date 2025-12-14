package com.virtualinterviewer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewStartRequest {
    private String jobRole;
    private String domain;
    private Integer numberOfQuestions;
    private String resumeContent;
}
