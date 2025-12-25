package com.virtualinterviewer.controller;

import com.virtualinterviewer.model.InterviewQuestion;
import com.virtualinterviewer.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/admin/questions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
public class AdminQuestionController {

    private final QuestionService questionService;

    public AdminQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createQuestion(@RequestBody InterviewQuestion question) {
        try {
            InterviewQuestion savedQuestion = questionService.createQuestion(question);
            return ResponseEntity.ok(savedQuestion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @RequestBody InterviewQuestion question) {
        try {
            Optional<InterviewQuestion> existingQuestion = questionService.getQuestionById(id);
            if (existingQuestion.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            question.setId(id);
            InterviewQuestion updatedQuestion = questionService.updateQuestion(question);
            return ResponseEntity.ok(updatedQuestion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        try {
            Optional<InterviewQuestion> question = questionService.getQuestionById(id);
            if (question.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            questionService.deleteQuestion(id);
            return ResponseEntity.ok("Question deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
