package com.virtualinterviewer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "interviews")
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String jobRole;

    @Column(nullable = false)
    private String domain; // DSA, System Design, HR, etc.

    @Enumerated(EnumType.STRING)
    private InterviewStatus status = InterviewStatus.IN_PROGRESS;

    @Column(nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    private LocalDateTime endTime;

    private Integer totalQuestions;

    private Integer questionsAnswered = 0;

    private Double overallScore = 0.0;

    @Column(columnDefinition = "TEXT")
    private String resumeContextUsed;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Answer> answers;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Feedback> feedbacks;

    // Persist the exact list/order of question IDs for this interview
    @ElementCollection
    @CollectionTable(name = "interview_question_links", joinColumns = @JoinColumn(name = "interview_id"))
    @Column(name = "question_id")
    private List<Long> questionIds = new ArrayList<>();

    public enum InterviewStatus {
        IN_PROGRESS, COMPLETED, PAUSED, ABANDONED
    }

    public Interview() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public InterviewStatus getStatus() { return status; }
    public void setStatus(InterviewStatus status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getQuestionsAnswered() { return questionsAnswered; }
    public void setQuestionsAnswered(Integer questionsAnswered) { this.questionsAnswered = questionsAnswered; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }

    public String getResumeContextUsed() { return resumeContextUsed; }
    public void setResumeContextUsed(String resumeContextUsed) { this.resumeContextUsed = resumeContextUsed; }

    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }

    public List<Feedback> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<Feedback> feedbacks) { this.feedbacks = feedbacks; }

    public List<Long> getQuestionIds() { return questionIds; }
    public void setQuestionIds(List<Long> questionIds) { this.questionIds = questionIds; }
}
