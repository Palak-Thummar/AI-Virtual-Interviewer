package com.virtualinterviewer.repository;

import com.virtualinterviewer.model.Interview;
import com.virtualinterviewer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByUser(User user);
    List<Interview> findByUserOrderByStartTimeDesc(User user);
}
