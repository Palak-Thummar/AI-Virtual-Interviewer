package com.virtualinterviewer.repository;

import com.virtualinterviewer.model.Feedback;
import com.virtualinterviewer.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByInterview(Interview interview);
}
