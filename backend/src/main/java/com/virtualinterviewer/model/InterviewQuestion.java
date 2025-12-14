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
@Table(name = "interview_questions")
public class InterviewQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @Column(nullable = false)
    private String domain; // e.g., "DSA", "System Design", "HR", "Java"

    @Column(nullable = false)
    private String jobRole; // e.g., "Software Engineer", "Data Scientist"

    @Column(columnDefinition = "TEXT")
    private String expectedAnswer;

    @Column(columnDefinition = "TEXT")
    private String hints;

    private Integer difficulty; // 1-5 scale

    private Integer timeLimitSeconds;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean isActive = true;

    @Column(nullable = false)
    private String createdBy;

    public enum QuestionType {
        TECHNICAL, BEHAVIORAL, CODING
    }
}
