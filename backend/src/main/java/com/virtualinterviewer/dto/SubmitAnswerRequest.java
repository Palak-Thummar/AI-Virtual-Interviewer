package com.virtualinterviewer.dto;

public class SubmitAnswerRequest {
    private Long interviewId;
    private Long questionId;
    private String answerText;
    private String answerAudio;
    private Integer timeTakenSeconds;

    public Long getInterviewId() { return interviewId; }
    public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }

    public String getAnswerAudio() { return answerAudio; }
    public void setAnswerAudio(String answerAudio) { this.answerAudio = answerAudio; }

    public Integer getTimeTakenSeconds() { return timeTakenSeconds; }
    public void setTimeTakenSeconds(Integer timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }
}
