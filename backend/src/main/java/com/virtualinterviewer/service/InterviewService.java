package com.virtualinterviewer.service;

import com.virtualinterviewer.model.*;
import com.virtualinterviewer.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final AnalyticsRepository analyticsRepository;
    private final AIService aiService;

    // Constructor-based dependency injection
    public InterviewService(
            InterviewRepository interviewRepository,
            InterviewQuestionRepository questionRepository,
            AnswerRepository answerRepository,
            FeedbackRepository feedbackRepository,
            AnalyticsRepository analyticsRepository,
            AIService aiService) {
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.feedbackRepository = feedbackRepository;
        this.analyticsRepository = analyticsRepository;
        this.aiService = aiService;
    }
    @Transactional
    public Interview startInterview(User user, String jobRole, String domain, Integer numberOfQuestions, String resumeContent) {
        try {
            Interview interview = new Interview();
            interview.setUser(user);
            interview.setJobRole(jobRole);
            interview.setDomain(domain);
            interview.setStatus(Interview.InterviewStatus.IN_PROGRESS);
            interview.setResumeContextUsed(resumeContent);

            Interview savedInterview = interviewRepository.save(interview);

            // Generate questions dynamically using AI and bind to this interview
            List<Long> qIds = generateQuestionsForInterview(savedInterview, numberOfQuestions, resumeContent);
            savedInterview.setQuestionIds(qIds);
            savedInterview.setTotalQuestions(qIds != null ? qIds.size() : 0);
            return interviewRepository.save(savedInterview);
        } catch (Exception e) {
            System.err.println("Error starting interview: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to start interview: " + e.getMessage(), e);
        }
    }

    private List<Long> generateQuestionsForInterview(Interview interview, Integer count, String resumeContent) {
        List<Long> ids = new ArrayList<>();
        String[] difficultyLabels = {"Easy", "Medium", "Hard"};
        
        // Pre-defined fallback questions for various domains
        String[][] fallbackQuestionsByDomain = {
            {"Tell me about your experience with " + interview.getDomain() + " in a " + interview.getJobRole() + " role.",
             "Describe a challenging " + interview.getDomain() + " problem you've solved as a " + interview.getJobRole() + ".",
             "What are your strongest skills related to " + interview.getDomain() + "? Provide examples.",
             "How do you approach learning new concepts in " + interview.getDomain() + "?",
             "Explain a recent project where you applied " + interview.getDomain() + " knowledge.",
             "What tools and technologies do you use for " + interview.getDomain() + "?",
             "How do you stay updated with latest trends in " + interview.getDomain() + "?",
             "Describe your problem-solving approach in " + interview.getDomain() + "."}
        };
        
        for (int i = 0; i < count; i++) {
            int diffIdx = i % difficultyLabels.length;
            int difficultyScale = (i % 5) + 1; // 1-5 cycling
            String questionText = null;
            String createdBy = "FALLBACK_SYSTEM";
            
            // Try AI generation first
            try {
                questionText = aiService.generateQuestion(
                        interview.getJobRole(),
                        interview.getDomain(),
                        difficultyLabels[diffIdx],
                        resumeContent
                );
                createdBy = "AI_SYSTEM";
                System.out.println("Successfully generated AI question " + (i + 1));
            } catch (Exception e) {
                System.err.println("AI question generation failed for question " + (i + 1) + ", using fallback: " + e.getMessage());
                // Use fallback
                questionText = fallbackQuestionsByDomain[0][i % fallbackQuestionsByDomain[0].length];
            }

            InterviewQuestion question = new InterviewQuestion();
            question.setQuestion(questionText);
            question.setDomain(interview.getDomain());
            question.setJobRole(interview.getJobRole());
            question.setType(InterviewQuestion.QuestionType.TECHNICAL);
            question.setDifficulty(difficultyScale);
            question.setTimeLimitSeconds(120);
            question.setActive(true);
            question.setCreatedBy(createdBy);

            try {
                InterviewQuestion saved = questionRepository.save(question);
                ids.add(saved.getId());
            } catch (Exception e) {
                System.err.println("Failed to save question " + (i + 1) + ": " + e.getMessage());
                throw new RuntimeException("Database error while saving questions", e);
            }
        }
        
        System.out.println("Generated " + ids.size() + " questions for interview " + interview.getId());
        return ids;
    }

    public List<InterviewQuestion> getQuestionsForInterview(String domain, String jobRole, Integer count) {
        List<InterviewQuestion> allQuestions = questionRepository.findByDomainAndJobRole(domain, jobRole);
        return allQuestions.stream().limit(count).toList();
    }

    public InterviewQuestion getNextQuestion(Interview interview) {
        // Hard stop if interview is finished or over limit
        if (interview.getStatus() != Interview.InterviewStatus.IN_PROGRESS ||
                (interview.getQuestionsAnswered() != null && interview.getTotalQuestions() != null &&
                        interview.getQuestionsAnswered() >= interview.getTotalQuestions())) {
            return null;
        }
        // Get IDs of questions already answered in this interview
        List<Long> answeredQuestionIds = answerRepository.findByInterview(interview)
                .stream()
                .map(answer -> answer.getQuestion().getId())
                .toList();

        List<Long> planIds = interview.getQuestionIds() != null ? interview.getQuestionIds() : List.of();
        if (planIds.isEmpty()) {
            return null;
        }

        // Load all planned questions and pick the first unanswered in planned order
        var loaded = questionRepository.findAllById(planIds);
        var byId = loaded.stream().collect(java.util.stream.Collectors.toMap(InterviewQuestion::getId, q -> q));
        for (Long qid : planIds) {
            if (!answeredQuestionIds.contains(qid)) {
                InterviewQuestion q = byId.get(qid);
                if (q != null) return q;
            }
        }
        return null;
    }

    @Transactional
    public Answer submitAnswer(Interview interview, InterviewQuestion question, String answerText, String audioPath, Integer timeTaken) {
        if (interview.getStatus() != Interview.InterviewStatus.IN_PROGRESS) {
            throw new RuntimeException("Interview is not in progress");
        }

        if (interview.getTotalQuestions() != null && interview.getQuestionsAnswered() != null &&
                interview.getQuestionsAnswered() >= interview.getTotalQuestions()) {
            throw new RuntimeException("All questions already answered for this interview");
        }

        // Ensure this question belongs to this interview plan
        List<Long> planIds = interview.getQuestionIds() != null ? interview.getQuestionIds() : List.of();
        if (!planIds.contains(question.getId())) {
            throw new RuntimeException("Question does not belong to this interview");
        }

        // Prevent duplicate submissions for the same question
        if (answerRepository.existsByInterviewAndQuestion(interview, question)) {
            throw new RuntimeException("Answer already submitted for this question");
        }
        Answer answer = new Answer();
        answer.setInterview(interview);
        answer.setQuestion(question);
        answer.setAnswerText(answerText);
        answer.setAnswerAudio(audioPath);
        answer.setTimeTakenSeconds(timeTaken);

        // Get AI evaluation with error handling
        String evaluation;
        double score;
        try {
            evaluation = aiService.evaluateAnswer(question.getQuestion(), answerText, interview.getDomain());
            score = extractScoreFromEvaluation(evaluation);
        } catch (Exception e) {
            System.err.println("AI evaluation failed: " + e.getMessage());
            // Fallback: Simple scoring based on answer length
            int wordCount = answerText != null ? answerText.trim().split("\\s+").length : 0;
            score = Math.min(wordCount * 2.5, 85); // Simple word-based scoring
            evaluation = String.format("Score: %.0f/100. AI evaluation unavailable - using basic scoring.", score);
        }
        
        answer.setAiEvaluation(evaluation);
        answer.setScore(score);

        Answer savedAnswer = answerRepository.save(answer);

        // Update interview
        interview.setQuestionsAnswered((interview.getQuestionsAnswered() == null ? 0 : interview.getQuestionsAnswered()) + 1);
        interviewRepository.save(interview);

        return savedAnswer;
    }

    @Transactional
    public Interview completeInterview(Long interviewId) {
        Optional<Interview> interviewOpt = interviewRepository.findById(interviewId);
        if (interviewOpt.isEmpty()) {
            throw new RuntimeException("Interview not found");
        }

        Interview interview = interviewOpt.get();
        interview.setStatus(Interview.InterviewStatus.COMPLETED);
        interview.setEndTime(LocalDateTime.now());

        // Calculate overall score
        List<Answer> answers = answerRepository.findByInterview(interview);
        double totalScore = answers.stream()
                .mapToDouble(a -> a.getScore() != null ? a.getScore() : 0)
                .average()
                .orElse(0);

        interview.setOverallScore(totalScore);
        Interview savedInterview = interviewRepository.save(interview);

        // Generate and save feedback (with error handling)
        try {
            generateFeedback(interview, answers);
        } catch (Exception e) {
            System.err.println("Failed to generate AI feedback: " + e.getMessage());
            // Create fallback feedback
            generateFallbackFeedback(interview, answers);
        }

        // Update analytics
        updateAnalytics(interview.getUser(), interview, totalScore);

        return savedInterview;
    }

    @Transactional
    private void generateFeedback(Interview interview, List<Answer> answers) {
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        Map<String, List<Double>> domainScores = new HashMap<>();

        // Analyze performance by domain
        for (Answer answer : answers) {
            if (answer.getScore() != null && answer.getQuestion() != null) {
                String domain = answer.getQuestion().getDomain();
                double score = answer.getScore();
                
                domainScores.computeIfAbsent(domain, k -> new ArrayList<>()).add(score);
                
                if (score >= 75) {
                    strengths.add("Strong performance in " + domain + " (scored " + Math.round(score) + "%)");
                } else if (score < 60) {
                    weaknesses.add("Needs improvement in " + domain + " (scored " + Math.round(score) + "%)");
                }
            }
        }

        // Generate comprehensive feedback
        String feedbackText = aiService.generateFeedback(strengths, weaknesses, interview.getOverallScore());

        Feedback feedback = new Feedback();
        feedback.setInterview(interview);
        feedback.setOverallScore(interview.getOverallScore());
        feedback.setOverallComments(feedbackText);
        feedback.setStrengths(String.join(", ", strengths));
        feedback.setWeaknesses(String.join(", ", weaknesses));
        feedback.setGeneratedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);
        
        // Update analytics with domain-based strengths/weaknesses
        updateAnalyticsStrengthsWeaknesses(interview.getUser(), domainScores);
    }

    private void generateFallbackFeedback(Interview interview, List<Answer> answers) {
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        Map<String, List<Double>> domainScores = new HashMap<>();

        // Analyze performance by domain
        for (Answer answer : answers) {
            if (answer.getScore() != null && answer.getQuestion() != null) {
                String domain = answer.getQuestion().getDomain();
                double score = answer.getScore();
                
                domainScores.computeIfAbsent(domain, k -> new ArrayList<>()).add(score);
                
                if (score >= 75) {
                    strengths.add("Strong performance in " + domain);
                } else if (score < 60) {
                    weaknesses.add("Needs improvement in " + domain);
                }
            }
        }

        String feedbackText = String.format(
            "Interview completed with an overall score of %.1f/100.\n\n" +
            "Strengths: %s\n\n" +
            "Areas for improvement: %s\n\n" +
            "Keep practicing and preparing for your next interview!",
            interview.getOverallScore(),
            strengths.isEmpty() ? "Continue building your skills" : String.join(", ", strengths),
            weaknesses.isEmpty() ? "Great work overall" : String.join(", ", weaknesses)
        );

        Feedback feedback = new Feedback();
        feedback.setInterview(interview);
        feedback.setOverallScore(interview.getOverallScore());
        feedback.setOverallComments(feedbackText);
        feedback.setStrengths(String.join(", ", strengths));
        feedback.setWeaknesses(String.join(", ", weaknesses));
        feedback.setGeneratedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);
        
        // Update analytics with domain-based strengths/weaknesses
        updateAnalyticsStrengthsWeaknesses(interview.getUser(), domainScores);
    }

    private void updateAnalytics(User user, Interview interview, double score) {
        Optional<Analytics> analyticsOpt = analyticsRepository.findByUser(user);
        Analytics analytics;
        
        if (analyticsOpt.isEmpty()) {
            // Create new analytics if doesn't exist
            analytics = new Analytics();
            analytics.setUser(user);
            analytics.setTotalInterviews(0);
            analytics.setCompletedInterviews(0);
            analytics.setAverageScore(0.0);
            analytics.setBestScore(0.0);
            analytics.setWorstScore(0.0);
        } else {
            analytics = analyticsOpt.get();
        }
        
        // Safely get current values with null checks
        double currentAvg = analytics.getAverageScore() != null ? analytics.getAverageScore() : 0.0;
        int oldTotal = analytics.getTotalInterviews() != null ? analytics.getTotalInterviews() : 0;
        int oldCompleted = analytics.getCompletedInterviews() != null ? analytics.getCompletedInterviews() : 0;
        double oldBest = analytics.getBestScore() != null ? analytics.getBestScore() : 0.0;
        double oldWorst = analytics.getWorstScore() != null ? analytics.getWorstScore() : 0.0;
        
        // Calculate new average
        double newAvg = oldTotal == 0 ? score : ((currentAvg * oldTotal) + score) / (oldTotal + 1);
        analytics.setAverageScore(newAvg);
        
        // Increment totals
        analytics.setTotalInterviews(oldTotal + 1);
        analytics.setCompletedInterviews(oldCompleted + 1);

        // Update best and worst scores
        if (oldBest == 0.0 || score > oldBest) {
            analytics.setBestScore(score);
        }
        if (oldWorst == 0.0 || score < oldWorst) {
            analytics.setWorstScore(score);
        }

        analytics.setLastInterviewDate(LocalDateTime.now());
        analytics.setLastUpdated(LocalDateTime.now());

        analyticsRepository.save(analytics);
        System.out.println("Analytics updated: Total=" + analytics.getTotalInterviews() + ", Avg=" + analytics.getAverageScore());
    }

    private void updateAnalyticsStrengthsWeaknesses(User user, Map<String, List<Double>> domainScores) {
        Optional<Analytics> analyticsOpt = analyticsRepository.findByUser(user);
        if (analyticsOpt.isEmpty()) {
            return;
        }
        
        Analytics analytics = analyticsOpt.get();
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        
        // Analyze each domain
        for (Map.Entry<String, List<Double>> entry : domainScores.entrySet()) {
            String domain = entry.getKey();
            List<Double> scores = entry.getValue();
            double avgScore = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            
            if (avgScore >= 75) {
                strengths.add(domain + " (" + Math.round(avgScore) + "%)");
            } else if (avgScore < 60) {
                weaknesses.add(domain + " (" + Math.round(avgScore) + "%)");
            }
        }
        
        // Update or append to existing
        String currentStrengths = analytics.getTopicStrengths();
        String currentWeaknesses = analytics.getTopicWeaknesses();
        
        if (currentStrengths != null && !currentStrengths.isEmpty()) {
            analytics.setTopicStrengths(currentStrengths + ", " + String.join(", ", strengths));
        } else {
            analytics.setTopicStrengths(String.join(", ", strengths));
        }
        
        if (currentWeaknesses != null && !currentWeaknesses.isEmpty()) {
            analytics.setTopicWeaknesses(currentWeaknesses + ", " + String.join(", ", weaknesses));
        } else {
            analytics.setTopicWeaknesses(String.join(", ", weaknesses));
        }
        
        analyticsRepository.save(analytics);
    }

    public List<Interview> getUserInterviews(User user) {
        return interviewRepository.findByUserOrderByStartTimeDesc(user);
    }

    public Optional<Interview> getInterviewById(Long id) {
        return interviewRepository.findById(id);
    }

    private double extractScoreFromEvaluation(String evaluation) {
        // Simplified extraction - in production, use proper parsing
        try {
            if (evaluation.contains("Score:")) {
                String scoreStr = evaluation.substring(evaluation.indexOf("Score:") + 6, evaluation.indexOf("Score:") + 10).trim();
                return Double.parseDouble(scoreStr.split("/")[0].trim());
            }
        } catch (Exception e) {
            return 0;
        }
        return 50; // Default score
    }

    private double calculateFallbackScore(String answerText, Integer difficulty) {
        if (answerText == null || answerText.trim().isEmpty()) {
            return 0;
        }

        // Base score on answer length
        int wordCount = answerText.trim().split("\\s+").length;
        double baseScore = Math.min(wordCount * 2, 70); // Max 70 from length

        // Adjust for difficulty (1=easiest, 5=hardest)
        double difficultyMultiplier = 1.0;
        if (difficulty != null) {
            if (difficulty <= 2) {
                difficultyMultiplier = 1.1; // Easier questions
            } else if (difficulty >= 4) {
                difficultyMultiplier = 0.9; // Harder questions
            }
        }

        // Add bonus for longer, detailed answers
        if (wordCount > 50) {
            baseScore += 10;
        }
        if (wordCount > 100) {
            baseScore += 10;
        }

        return Math.min(baseScore * difficultyMultiplier, 100);
    }
}
