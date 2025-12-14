package com.virtualinterviewer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interviews")
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
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
    private List<Answer> answers;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

    public enum InterviewStatus {
        IN_PROGRESS, COMPLETED, PAUSED, ABANDONED
    }
}
