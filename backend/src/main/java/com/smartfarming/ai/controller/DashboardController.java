package com.smartfarming.ai.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartfarming.ai.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /** Real 5-model comparison from the actual Python benchmark run — see /ml/model-results. */
    @GetMapping("/model-comparison")
    public JsonNode modelComparison() {
        return dashboardService.getCropModelComparison();
    }

    @GetMapping("/feature-importance")
    public JsonNode featureImportance() {
        return dashboardService.getCropFeatureImportance();
    }

    /** The honest ~14% result from the rejected fertilizer ML attempt, shown transparently. */
    @GetMapping("/fertilizer-ml-attempt")
    public JsonNode fertilizerAttempt() {
        return dashboardService.getFertilizerAttemptMetrics();
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return Map.of(
                "cropModelComparison", dashboardService.getCropModelComparison(),
                "cropFeatureImportance", dashboardService.getCropFeatureImportance(),
                "fertilizerMlAttempt", dashboardService.getFertilizerAttemptMetrics()
        );
    }
}
