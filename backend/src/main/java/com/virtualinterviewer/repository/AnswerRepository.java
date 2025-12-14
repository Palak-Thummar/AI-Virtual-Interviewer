package com.virtualinterviewer.repository;

import com.virtualinterviewer.model.Answer;
import com.virtualinterviewer.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByInterview(Interview interview);
}
