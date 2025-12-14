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
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private InterviewQuestion question;

    @Column(columnDefinition = "LONGTEXT")
    private String answerText;

    private String answerAudio; // Path to audio file

    private Integer timeTakenSeconds;

    @Column(nullable = false)
    private LocalDateTime answeredAt = LocalDateTime.now();

    private Double score; // 0-100

    @Column(columnDefinition = "TEXT")
    private String aiEvaluation;

    private boolean isCorrect;
}
