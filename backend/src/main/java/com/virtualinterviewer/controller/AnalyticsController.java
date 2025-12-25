package com.virtualinterviewer.controller;

import com.virtualinterviewer.model.Analytics;
import com.virtualinterviewer.model.Interview;
import com.virtualinterviewer.service.AnalyticsService;
import com.virtualinterviewer.service.AuthService;
import com.virtualinterviewer.service.InterviewService;
import com.virtualinterviewer.model.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AuthService authService;
    private final InterviewService interviewService;

    public AnalyticsController(AnalyticsService analyticsService, AuthService authService, InterviewService interviewService) {
        this.analyticsService = analyticsService;
        this.authService = authService;
        this.interviewService = interviewService;
    }

    @GetMapping("/my-analytics")
    @Transactional
    public ResponseEntity<?> getMyAnalytics(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Error: Unauthorized - No valid authentication");
            }

            Object principal = authentication.getPrincipal();
            String userEmail;
            if (principal instanceof UserDetails) {
                userEmail = ((UserDetails) principal).getUsername();
            } else {
                userEmail = principal.toString();
            }

            User user = authService.getUserByEmail(userEmail);
            if (user == null) {
                return ResponseEntity.status(404).body("Error: User not found for email: " + userEmail);
            }
            
            Analytics analytics = analyticsService.getUserAnalytics(user);
            
            // Get interview history for detailed analytics
            List<Interview> interviews = interviewService.getUserInterviews(user);
            if (interviews == null) {
                interviews = new ArrayList<>();
            }
            
            List<Interview> completedInterviews = interviews.stream()
                    .filter(i -> i != null && i.getStatus() == Interview.InterviewStatus.COMPLETED)
                    .filter(i -> i.getStartTime() != null)
                    .sorted((i1, i2) -> i1.getStartTime().compareTo(i2.getStartTime()))
                    .collect(Collectors.toList());
            
            // Build detailed analytics response
            Map<String, Object> detailedAnalytics = new HashMap<>();
            detailedAnalytics.put("totalInterviews", analytics.getTotalInterviews() != null ? analytics.getTotalInterviews() : 0);
            detailedAnalytics.put("completedInterviews", analytics.getCompletedInterviews() != null ? analytics.getCompletedInterviews() : 0);
            detailedAnalytics.put("averageScore", analytics.getAverageScore() != null ? Math.round(analytics.getAverageScore() * 10.0) / 10.0 : 0.0);
            detailedAnalytics.put("bestScore", analytics.getBestScore() != null ? Math.round(analytics.getBestScore() * 10.0) / 10.0 : 0.0);
            detailedAnalytics.put("worstScore", analytics.getWorstScore() != null && analytics.getWorstScore() > 0 ? Math.round(analytics.getWorstScore() * 10.0) / 10.0 : 0.0);
            detailedAnalytics.put("topicStrengths", analytics.getTopicStrengths() != null ? analytics.getTopicStrengths() : "");
            detailedAnalytics.put("topicWeaknesses", analytics.getTopicWeaknesses() != null ? analytics.getTopicWeaknesses() : "");
            detailedAnalytics.put("lastInterviewDate", analytics.getLastInterviewDate());
            
            // Add interview history with scores
            List<Map<String, Object>> interviewHistory = completedInterviews.stream()
                    .limit(10) // Last 10 interviews
                    .map(interview -> {
                        Map<String, Object> interviewData = new HashMap<>();
                        interviewData.put("id", interview.getId());
                        interviewData.put("jobRole", interview.getJobRole() != null ? interview.getJobRole() : "Unknown");
                        interviewData.put("domain", interview.getDomain() != null ? interview.getDomain() : "General");
                        double roundedScore = interview.getOverallScore() != null ? Math.round(interview.getOverallScore() * 10.0) / 10.0 : 0.0;
                        interviewData.put("score", roundedScore);
                        interviewData.put("date", interview.getStartTime());
                        return interviewData;
                    })
                    .collect(Collectors.toList());
            
            detailedAnalytics.put("interviewHistory", interviewHistory);
            
            // Calculate domain-wise performance
            Map<String, Double> domainPerformance = completedInterviews.stream()
                    .filter(i -> i != null && i.getOverallScore() != null && i.getDomain() != null)
                    .collect(Collectors.groupingBy(
                            Interview::getDomain,
                            Collectors.averagingDouble(Interview::getOverallScore)
                    ));
            
            detailedAnalytics.put("domainPerformance", domainPerformance);
            
            return ResponseEntity.ok(detailedAnalytics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/recalculate")
    @Transactional
    public ResponseEntity<?> recalculateAnalytics(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Error: Unauthorized");
            }

            String userEmail = authentication.getName();
            User user = authService.getUserByEmail(userEmail);
            
            // Get all user's interviews
            List<Interview> allInterviews = interviewService.getUserInterviews(user);
            List<Interview> completed = allInterviews.stream()
                    .filter(i -> i.getStatus() == Interview.InterviewStatus.COMPLETED)
                    .toList();
            
            // Reset and recalculate analytics from scratch
            Analytics analytics = analyticsService.getUserAnalytics(user);
            
            // Reset all values
            analytics.setTotalInterviews(allInterviews.size());
            analytics.setCompletedInterviews(completed.size());
            analytics.setAverageScore(0.0);
            analytics.setBestScore(0.0);
            analytics.setWorstScore(0.0);
            
            if (completed.size() > 0) {
                // Get all valid scores (filter out nulls and zeros from incomplete answers)
                java.util.stream.DoubleStream validScores = completed.stream()
                        .filter(i -> i.getOverallScore() != null && i.getOverallScore() > 0)
                        .mapToDouble(Interview::getOverallScore);
                
                double[] scores = validScores.toArray();
                if (scores.length > 0) {
                    double avgScore = java.util.Arrays.stream(scores).average().orElse(0.0);
                    double bestScore = java.util.Arrays.stream(scores).max().orElse(0.0);
                    double worstScore = java.util.Arrays.stream(scores).min().orElse(0.0);
                    
                    analytics.setAverageScore(Math.round(avgScore * 10.0) / 10.0);
                    analytics.setBestScore(Math.round(bestScore * 10.0) / 10.0);
                    analytics.setWorstScore(Math.round(worstScore * 10.0) / 10.0);
                }
                
                // Set last interview date
                completed.stream()
                        .filter(i -> i.getStartTime() != null)
                        .max((i1, i2) -> i1.getStartTime().compareTo(i2.getStartTime()))
                        .ifPresent(i -> analytics.setLastInterviewDate(i.getStartTime()));
            }
            
            analytics.setLastUpdated(java.time.LocalDateTime.now());
            analyticsService.updateAnalytics(analytics);
            
            System.out.println("Analytics recalculated: Total=" + analytics.getTotalInterviews() + 
                             ", Completed=" + analytics.getCompletedInterviews() + 
                             ", Avg=" + analytics.getAverageScore() + 
                             ", Best=" + analytics.getBestScore() +
                             ", Worst=" + analytics.getWorstScore());
            
            return ResponseEntity.ok(Map.of(
                "message", "Analytics recalculated successfully",
                "totalInterviews", analytics.getTotalInterviews(),
                "completedInterviews", analytics.getCompletedInterviews(),
                "averageScore", analytics.getAverageScore(),
                "bestScore", analytics.getBestScore(),
                "worstScore", analytics.getWorstScore()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
