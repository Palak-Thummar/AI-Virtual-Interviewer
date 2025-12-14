package com.virtualinterviewer.repository;

import com.virtualinterviewer.model.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findByDomainAndJobRole(String domain, String jobRole);
    List<InterviewQuestion> findByTypeAndDomain(String type, String domain);
    List<InterviewQuestion> findAllByIsActiveTrue();
    List<InterviewQuestion> findByDomainAndDifficultyAndIsActiveTrue(String domain, Integer difficulty);
}
