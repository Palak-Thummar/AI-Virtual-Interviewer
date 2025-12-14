package com.virtualinterviewer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {
    private Long interviewId;
    private Long questionId;
    private String answerText;
    private String answerAudio;
    private Integer timeTakenSeconds;
}
