package com.virtualinterviewer.controller;

import com.virtualinterviewer.model.InterviewQuestion;
import com.virtualinterviewer.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/questions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/public/all")
    public ResponseEntity<?> getAllQuestions() {
        try {
            List<InterviewQuestion> questions = questionService.getAllActiveQuestions();
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/public/domain/{domain}/role/{jobRole}")
    public ResponseEntity<?> getQuestionsByDomainAndRole(
            @PathVariable String domain,
            @PathVariable String jobRole) {
        try {
            List<InterviewQuestion> questions = questionService.getQuestionsByDomainAndJobRole(domain, jobRole);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/public/type/{type}/domain/{domain}")
    public ResponseEntity<?> getQuestionsByType(
            @PathVariable String type,
            @PathVariable String domain) {
        try {
            List<InterviewQuestion> questions = questionService.getQuestionsByType(type, domain);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        try {
            Optional<InterviewQuestion> question = questionService.getQuestionById(id);
            if (question.isPresent()) {
                return ResponseEntity.ok(question.get());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/public/difficulty/{domain}/{difficulty}")
    public ResponseEntity<?> getQuestionsByDifficulty(
            @PathVariable String domain,
            @PathVariable Integer difficulty) {
        try {
            List<InterviewQuestion> questions = questionService.getQuestionsByDifficultyLevel(domain, difficulty);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
