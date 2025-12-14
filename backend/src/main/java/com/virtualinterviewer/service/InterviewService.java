package com.virtualinterviewer.service;

import com.virtualinterviewer.model.*;
import com.virtualinterviewer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final AnalyticsRepository analyticsRepository;
    private final AIService aiService;

    @Transactional
    public Interview startInterview(User user, String jobRole, String domain, Integer numberOfQuestions, String resumeContent) {
        Interview interview = new Interview();
        interview.setUser(user);
        interview.setJobRole(jobRole);
        interview.setDomain(domain);
        interview.setTotalQuestions(numberOfQuestions);
        interview.setStatus(Interview.InterviewStatus.IN_PROGRESS);
        interview.setResumeContextUsed(resumeContent);

        return interviewRepository.save(interview);
    }

    public List<InterviewQuestion> getQuestionsForInterview(String domain, String jobRole, Integer count) {
        List<InterviewQuestion> allQuestions = questionRepository.findByDomainAndJobRole(domain, jobRole);
        return allQuestions.stream().limit(count).toList();
    }

    public InterviewQuestion getNextQuestion(Interview interview) {
        List<InterviewQuestion> answeredQuestions = answerRepository.findByInterview(interview)
                .stream()
                .map(Answer::getQuestion)
                .toList();

        List<InterviewQuestion> availableQuestions = questionRepository.findByDomainAndJobRole(
                interview.getDomain(),
                interview.getJobRole()
        ).stream()
                .filter(q -> !answeredQuestions.contains(q))
                .toList();

        if (availableQuestions.isEmpty()) {
            return null;
        }

        return availableQuestions.get(0);
    }

    @Transactional
    public Answer submitAnswer(Interview interview, InterviewQuestion question, String answerText, String audioPath, Integer timeTaken) {
        Answer answer = new Answer();
        answer.setInterview(interview);
        answer.setQuestion(question);
        answer.setAnswerText(answerText);
        answer.setAnswerAudio(audioPath);
        answer.setTimeTakenSeconds(timeTaken);

        // Get AI evaluation
        String evaluation = aiService.evaluateAnswer(question.getQuestion(), answerText, interview.getDomain());
        answer.setAiEvaluation(evaluation);

        // Extract score from evaluation (simplified - in production, parse properly)
        answer.setScore(extractScoreFromEvaluation(evaluation));

        Answer savedAnswer = answerRepository.save(answer);

        // Update interview
        interview.setQuestionsAnswered(interview.getQuestionsAnswered() + 1);
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

        // Generate and save feedback
        generateFeedback(interview, answers);

        // Update analytics
        updateAnalytics(interview.getUser(), interview, totalScore);

        return savedInterview;
    }

    @Transactional
    private void generateFeedback(Interview interview, List<Answer> answers) {
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();

        // Extract from answers (simplified)
        for (Answer answer : answers) {
            if (answer.getScore() != null && answer.getScore() > 70) {
                strengths.add("Strong answer on " + answer.getQuestion().getDomain());
            } else {
                weaknesses.add("Needs improvement on " + answer.getQuestion().getDomain());
            }
        }

        String feedbackText = aiService.generateFeedback(strengths, weaknesses, interview.getOverallScore());

        Feedback feedback = new Feedback();
        feedback.setInterview(interview);
        feedback.setOverallScore(interview.getOverallScore());
        feedback.setOverallComments(feedbackText);
        feedback.setStrengths(String.join(", ", strengths));
        feedback.setWeaknesses(String.join(", ", weaknesses));
        feedback.setGeneratedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);
    }

    private void updateAnalytics(User user, Interview interview, double score) {
        Optional<Analytics> analyticsOpt = analyticsRepository.findByUser(user);
        if (analyticsOpt.isEmpty()) {
            return;
        }

        Analytics analytics = analyticsOpt.get();
        analytics.setTotalInterviews(analytics.getTotalInterviews() + 1);
        analytics.setCompletedInterviews(analytics.getCompletedInterviews() + 1);

        // Update average score
        double currentAvg = analytics.getAverageScore();
        analytics.setAverageScore((currentAvg * (analytics.getTotalInterviews() - 1) + score) / analytics.getTotalInterviews());

        // Update best and worst scores
        if (score > analytics.getBestScore()) {
            analytics.setBestScore(score);
        }
        if (score < analytics.getWorstScore() || analytics.getWorstScore() == 0) {
            analytics.setWorstScore(score);
        }

        analytics.setLastInterviewDate(LocalDateTime.now());
        analytics.setLastUpdated(LocalDateTime.now());

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
}
