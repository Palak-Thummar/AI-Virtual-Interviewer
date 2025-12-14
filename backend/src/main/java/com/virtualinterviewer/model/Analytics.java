package com.virtualinterviewer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "analytics")
public class Analytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer totalInterviews = 0;

    private Integer completedInterviews = 0;

    private Double averageScore = 0.0;

    private Double bestScore = 0.0;

    private Double worstScore = 0.0;

    @Column(columnDefinition = "TEXT")
    private String topicStrengths; // JSON format

    @Column(columnDefinition = "TEXT")
    private String topicWeaknesses; // JSON format

    private Integer totalTimeSpent = 0; // in seconds

    private LocalDateTime lastInterviewDate;

    private LocalDateTime lastUpdated = LocalDateTime.now();
}
