package com.virtualinterviewer.service;

import com.virtualinterviewer.model.InterviewQuestion;
import com.virtualinterviewer.repository.InterviewQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final InterviewQuestionRepository questionRepository;

    public List<InterviewQuestion> getAllActiveQuestions() {
        return questionRepository.findAllByIsActiveTrue();
    }

    public List<InterviewQuestion> getQuestionsByDomainAndJobRole(String domain, String jobRole) {
        return questionRepository.findByDomainAndJobRole(domain, jobRole);
    }

    public List<InterviewQuestion> getQuestionsByType(String type, String domain) {
        return questionRepository.findByTypeAndDomain(type, domain);
    }

    public Optional<InterviewQuestion> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public InterviewQuestion createQuestion(InterviewQuestion question) {
        return questionRepository.save(question);
    }

    public InterviewQuestion updateQuestion(InterviewQuestion question) {
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    public List<InterviewQuestion> getQuestionsByDifficultyLevel(String domain, Integer difficulty) {
        return questionRepository.findByDomainAndDifficultyAndIsActiveTrue(domain, difficulty);
    }
}
