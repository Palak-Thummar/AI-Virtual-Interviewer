package com.virtualinterviewer.dto;

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

    public InterviewQuestionDTO() {}

    public InterviewQuestionDTO(Long id, String question, String type, String domain, String jobRole, String expectedAnswer, String hints, Integer difficulty, Integer timeLimitSeconds) {
        this.id = id;
        this.question = question;
        this.type = type;
        this.domain = domain;
        this.jobRole = jobRole;
        this.expectedAnswer = expectedAnswer;
        this.hints = hints;
        this.difficulty = difficulty;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getExpectedAnswer() {
        return expectedAnswer;
    }

    public void setExpectedAnswer(String expectedAnswer) {
        this.expectedAnswer = expectedAnswer;
    }

    public String getHints() {
        return hints;
    }

    public void setHints(String hints) {
        this.hints = hints;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public void setTimeLimitSeconds(Integer timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }
}
