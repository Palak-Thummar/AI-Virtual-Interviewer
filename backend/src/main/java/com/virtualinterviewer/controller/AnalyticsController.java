package com.virtualinterviewer.controller;

import com.virtualinterviewer.model.Analytics;
import com.virtualinterviewer.service.AnalyticsService;
import com.virtualinterviewer.service.AuthService;
import com.virtualinterviewer.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AuthService authService;

    @GetMapping("/my-analytics")
    public ResponseEntity<?> getMyAnalytics(Authentication authentication) {
        try {
            User user = authService.getUserByEmail(authentication.getName());
            Analytics analytics = analyticsService.getUserAnalytics(user);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
