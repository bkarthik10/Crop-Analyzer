package com.smartfarming.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/** Output of POST /api/crop/predict. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropPredictionResponse {

    private String crop;
    private double confidence;                 // 0.0 - 1.0, share of Random Forest tree votes
    private List<AlternativeCrop> alternatives; // next-best crops, for context

    private String season;                      // most common APY season for this crop, if known
    private String imageUrl;

    private SoilHealth soilHealth;
    private FarmSnapshot farmSnapshot;           // null if no historical data exists for this crop

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlternativeCrop {
        private String crop;
        private double probability;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SoilHealth {
        private int score;                       // 0-100
        private Map<String, String> nutrientStatus; // "Nitrogen" -> "Optimal" | "Low" | "High"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FarmSnapshot {
        private String typicalSeason;
        private Double avgYieldTonPerHectare;
        private Double avgAreaHectare;
        private Double avgNetProfitInr;   // null if this crop isn't in the profit dataset's 6 tracked crops
        private Double avgRoiPercent;
    }
}
