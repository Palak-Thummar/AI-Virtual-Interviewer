package com.virtualinterviewer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionDTO {
    private Long id;
    private String question;
    private String type;
    private String domain;
    private String jobRole;
    private String expectedAnswer;
    private String hints;
    private Integer difficulty;
    private Integer timeLimitSeconds;
}
