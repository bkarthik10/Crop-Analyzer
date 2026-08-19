package com.smartfarming.ai.model;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fertilizer recommendation engine.
 *
 * <h2>Why this is rule-based, not ML</h2>
 * {@code Fertilizer_Prediction.csv} (100,000 rows) was benchmarked with
 * Logistic Regression, Decision Tree and Random Forest classifiers and every
 * one of them landed at ~14% accuracy — statistically indistinguishable from
 * a dummy classifier guessing the most frequent class (also ~14%, since the
 * 7 fertilizer labels are perfectly balanced). A crosstab of every feature
 * (Soil Type, Crop Type, Temperature, Humidity, Moisture, N, K, P) against
 * Fertilizer Name showed near-identical distributions across all classes —
 * i.e. the labels in this particular file are statistically independent of
 * the inputs, so there is nothing for a classifier to learn. See
 * {@code /ml/model-results/fertilizer_ml_attempt_metrics.json} for the raw
 * numbers behind this call.
 *
 * <p>Rather than ship a coin-flip model and call it "AI", this class picks
 * from the 7 real fertilizer formulations that <b>do</b> appear in the
 * dataset (so recommendations stay grounded in real products) using standard,
 * widely-taught agronomic logic: compare measured N/P/K against a healthy
 * range and recommend the formulation that best corrects the largest
 * deficit(s). If a better-labeled fertilizer dataset becomes available, swap
 * this class's {@link #recommend} method for a trained classifier — the rest
 * of the architecture (FertilizerService, controller, DTOs) doesn't change.
 */
@Component
public class FertilizerModel {

    /** Approximate NPK composition / plain-language usage notes — general agronomic knowledge, not dataset-derived. */
    private static final Map<String, String> FERTILIZER_NOTES = new LinkedHashMap<>();
    static {
        FERTILIZER_NOTES.put("Urea", "Approx. 46% nitrogen, no phosphorus/potassium. The standard choice when nitrogen is the clear limiting nutrient.");
        FERTILIZER_NOTES.put("DAP", "Di-ammonium phosphate, approx. 18% N / 46% P2O5. Used when phosphorus is notably deficient, often applied at sowing.");
        FERTILIZER_NOTES.put("14-35-14", "N-P-K blend weighted toward phosphorus. Suits soils low in both nitrogen and phosphorus with adequate potassium.");
        FERTILIZER_NOTES.put("10-26-26", "N-P-K blend weighted toward phosphorus and potassium. Suits soils where nitrogen is adequate but P and K are both low.");
        FERTILIZER_NOTES.put("17-17-17", "Balanced N-P-K blend. General-purpose maintenance feeding when no single nutrient is severely deficient.");
        FERTILIZER_NOTES.put("20-20", "Balanced, moderate-strength N-P blend for light-to-moderate combined nitrogen/phosphorus deficits.");
        FERTILIZER_NOTES.put("28-28", "High-strength N-P blend. Suits soils with pronounced nitrogen and phosphorus deficits and adequate potassium.");
    }

    /** Healthy reference midpoints used to compute deficits — broadly typical field-crop ranges (mg/kg). */
    private static final double TARGET_N = 90.0;
    private static final double TARGET_P = 45.0;
    private static final double TARGET_K = 45.0;

    public record Recommendation(String fertilizerName, String reasoning, String usageNote) {}

    /**
     * Chooses a fertilizer formulation from the 7 real products in the dataset
     * based on which of N/P/K is most deficient relative to a healthy target range.
     * If crop-specific optimal ranges are known (from {@code SoilHealthService}),
     * pass those instead of the defaults for a more tailored recommendation.
     */
    public Recommendation recommend(double n, double p, double k) {
        return recommend(n, p, k, TARGET_N, TARGET_P, TARGET_K);
    }

    public Recommendation recommend(double n, double p, double k, double targetN, double targetP, double targetK) {
        double deficitN = Math.max(0, targetN - n) / targetN;
        double deficitP = Math.max(0, targetP - p) / targetP;
        double deficitK = Math.max(0, targetK - k) / targetK;

        boolean nLow = deficitN > 0.20;
        boolean pLow = deficitP > 0.20;
        boolean kLow = deficitK > 0.20;

        String chosen;
        String reasoning;

        if (nLow && !pLow && !kLow) {
            chosen = "Urea";
            reasoning = String.format("Nitrogen is about %.0f%% below the healthy range while phosphorus and potassium are adequate.", deficitN * 100);
        } else if (pLow && !nLow && !kLow) {
            chosen = "DAP";
            reasoning = String.format("Phosphorus is about %.0f%% below the healthy range while nitrogen and potassium are adequate.", deficitP * 100);
        } else if (nLow && pLow && !kLow) {
            chosen = deficitP >= deficitN ? "14-35-14" : "28-28";
            reasoning = String.format("Both nitrogen (%.0f%% below target) and phosphorus (%.0f%% below target) are low, with potassium adequate.", deficitN * 100, deficitP * 100);
        } else if (pLow && kLow && !nLow) {
            chosen = "10-26-26";
            reasoning = String.format("Phosphorus (%.0f%% below target) and potassium (%.0f%% below target) are both low, with nitrogen adequate.", deficitP * 100, deficitK * 100);
        } else if (nLow && pLow && kLow) {
            chosen = "17-17-17";
            reasoning = "Nitrogen, phosphorus and potassium are all below the healthy range — a balanced blend addresses all three.";
        } else if (nLow && kLow && !pLow) {
            chosen = "17-17-17";
            reasoning = String.format("Nitrogen (%.0f%% below target) and potassium (%.0f%% below target) are low; a balanced blend covers both without over-applying phosphorus.", deficitN * 100, deficitK * 100);
        } else {
            chosen = "20-20";
            reasoning = "Soil nutrients are close to the healthy range — a light, balanced maintenance feeding is enough.";
        }

        return new Recommendation(chosen, reasoning, FERTILIZER_NOTES.getOrDefault(chosen, ""));
    }

    public Map<String, String> getFertilizerNotes() {
        return FERTILIZER_NOTES;
    }
}
