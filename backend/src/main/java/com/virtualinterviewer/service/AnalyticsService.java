package com.virtualinterviewer.service;

import com.virtualinterviewer.model.User;
import com.virtualinterviewer.repository.AnalyticsRepository;
import com.virtualinterviewer.model.Analytics;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public Analytics getUserAnalytics(User user) {
        Optional<Analytics> analyticsOpt = analyticsRepository.findByUser(user);
        if (analyticsOpt.isEmpty()) {
            Analytics analytics = new Analytics();
            analytics.setUser(user);
            return analyticsRepository.save(analytics);
        }
        return analyticsOpt.get();
    }

    public Analytics updateAnalytics(Analytics analytics) {
        return analyticsRepository.save(analytics);
    }
}
