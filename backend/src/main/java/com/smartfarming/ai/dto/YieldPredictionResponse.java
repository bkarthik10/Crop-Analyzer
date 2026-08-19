package com.smartfarming.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Output of POST /api/farm-analysis/yield-estimate. Reports R2/MAE, not an
 * "accuracy percentage" — this is a regression model, not a classifier.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YieldPredictionResponse {

    private double predictedYield;
    private double modelR2;
    private double modelMaeTonsPerHectare;
}
