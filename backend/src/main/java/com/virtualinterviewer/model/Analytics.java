package com.virtualinterviewer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_interviews")
    private Integer totalInterviews = 0;

    @Column(name = "completed_interviews")
    private Integer completedInterviews = 0;

    @Column(name = "average_score")
    private Double averageScore = 0.0;

    @Column(name = "best_score")
    private Double bestScore = 0.0;

    @Column(name = "worst_score")
    private Double worstScore = 0.0;

    @Column(name = "last_interview_date")
    private LocalDateTime lastInterviewDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Optional free-form topic summaries
    @Column(name = "topic_strengths", columnDefinition = "TEXT")
    private String topicStrengths;

    @Column(name = "topic_weaknesses", columnDefinition = "TEXT")
    private String topicWeaknesses;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    public Analytics() {}

    public Analytics(Long id, User user, Integer totalInterviews, Integer completedInterviews, Double averageScore, Double bestScore, Double worstScore, LocalDateTime lastInterviewDate, LocalDateTime createdAt, LocalDateTime lastUpdated) {
        this.id = id;
        this.user = user;
        this.totalInterviews = totalInterviews;
        this.completedInterviews = completedInterviews;
        this.averageScore = averageScore;
        this.bestScore = bestScore;
        this.worstScore = worstScore;
        this.lastInterviewDate = lastInterviewDate;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTotalInterviews() {
        return totalInterviews;
    }

    public void setTotalInterviews(Integer totalInterviews) {
        this.totalInterviews = totalInterviews;
    }

    public Integer getCompletedInterviews() {
        return completedInterviews;
    }

    public void setCompletedInterviews(Integer completedInterviews) {
        this.completedInterviews = completedInterviews;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getBestScore() {
        return bestScore;
    }

    public void setBestScore(Double bestScore) {
        this.bestScore = bestScore;
    }

    public Double getWorstScore() {
        return worstScore;
    }

    public void setWorstScore(Double worstScore) {
        this.worstScore = worstScore;
    }

    public LocalDateTime getLastInterviewDate() {
        return lastInterviewDate;
    }

    public void setLastInterviewDate(LocalDateTime lastInterviewDate) {
        this.lastInterviewDate = lastInterviewDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getTopicStrengths() {
        return topicStrengths;
    }

    public void setTopicStrengths(String topicStrengths) {
        this.topicStrengths = topicStrengths;
    }

    public String getTopicWeaknesses() {
        return topicWeaknesses;
    }

    public void setTopicWeaknesses(String topicWeaknesses) {
        this.topicWeaknesses = topicWeaknesses;
    }
}
