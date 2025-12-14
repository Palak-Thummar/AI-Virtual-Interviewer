package com.virtualinterviewer.repository;

import com.virtualinterviewer.model.Analytics;
import com.virtualinterviewer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    Optional<Analytics> findByUser(User user);
}
