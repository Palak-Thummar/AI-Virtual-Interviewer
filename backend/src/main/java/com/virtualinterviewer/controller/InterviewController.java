package com.virtualinterviewer.controller;

import com.virtualinterviewer.dto.InterviewStartRequest;
import com.virtualinterviewer.dto.SubmitAnswerRequest;
import com.virtualinterviewer.model.*;
import com.virtualinterviewer.service.AuthService;
import com.virtualinterviewer.service.InterviewService;
import com.virtualinterviewer.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class InterviewController {

    private final InterviewService interviewService;
    private final QuestionService questionService;
    private final AuthService authService;

    @PostMapping("/start")
    public ResponseEntity<?> startInterview(@RequestBody InterviewStartRequest request, Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            Interview interview = interviewService.startInterview(
                    user,
                    request.getJobRole(),
                    request.getDomain(),
                    request.getNumberOfQuestions(),
                    request.getResumeContent()
            );
            return ResponseEntity.ok(interview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{interviewId}")
    public ResponseEntity<?> getInterview(@PathVariable Long interviewId) {
        try {
            Optional<Interview> interview = interviewService.getInterviewById(interviewId);
            if (interview.isPresent()) {
                return ResponseEntity.ok(interview.get());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{interviewId}/next-question")
    public ResponseEntity<?> getNextQuestion(@PathVariable Long interviewId) {
        try {
            Optional<Interview> interviewOpt = interviewService.getInterviewById(interviewId);
            if (interviewOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Interview interview = interviewOpt.get();
            InterviewQuestion question = interviewService.getNextQuestion(interview);

            if (question == null) {
                return ResponseEntity.ok("All questions completed");
            }

            return ResponseEntity.ok(question);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{interviewId}/submit-answer")
    public ResponseEntity<?> submitAnswer(@PathVariable Long interviewId, @RequestBody SubmitAnswerRequest request) {
        try {
            Optional<Interview> interviewOpt = interviewService.getInterviewById(interviewId);
            if (interviewOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<InterviewQuestion> questionOpt = questionService.getQuestionById(request.getQuestionId());
            if (questionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Answer answer = interviewService.submitAnswer(
                    interviewOpt.get(),
                    questionOpt.get(),
                    request.getAnswerText(),
                    request.getAnswerAudio(),
                    request.getTimeTakenSeconds()
            );

            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{interviewId}/complete")
    public ResponseEntity<?> completeInterview(@PathVariable Long interviewId) {
        try {
            Interview interview = interviewService.completeInterview(interviewId);
            return ResponseEntity.ok(interview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/my-interviews")
    public ResponseEntity<?> getMyInterviews(Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            List<Interview> interviews = interviewService.getUserInterviews(user);
            return ResponseEntity.ok(interviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
