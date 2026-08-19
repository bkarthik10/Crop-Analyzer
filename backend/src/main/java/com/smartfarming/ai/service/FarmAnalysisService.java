package com.smartfarming.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfarming.ai.dto.FarmAnalysisResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Serves real historical crop statistics for the Farm Analysis page.
 *
 * <p>Both source datasets were pre-aggregated in Python (see
 * {@code /ml/notebooks/}) rather than loaded row-by-row here: APY.csv alone
 * is 345,336 rows and 20MB — computing a per-crop groupby in Java on every
 * request (or even once at startup) is unnecessary work when pandas already
 * did it once and the result is a 4KB JSON file. Only ~23 of the 40 crop-model
 * crops have a confident name match in APY (see the mapping in
 * export_final.py), and only 6 crops (Cotton, Maize, Potato, Rice, Sugarcane,
 * Wheat) appear in the profit dataset at all — for anything else, the
 * corresponding response fields come back {@code null} / {@code false} rather
 * than an invented number.
 */
@Service
public class FarmAnalysisService {

    private final Map<String, JsonNode> apyByCrop = new HashMap<>();
    private final Map<String, JsonNode> profitByCrop = new HashMap<>();

    @PostConstruct
    public void load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = new ClassPathResource("datasets/apy_crop_aggregates.json").getInputStream()) {
            for (JsonNode entry : mapper.readTree(is)) {
                apyByCrop.put(entry.get("crop").asText(), entry);
            }
        }

        try (InputStream is = new ClassPathResource("datasets/farm_profit_aggregates.json").getInputStream()) {
            for (JsonNode entry : mapper.readTree(is)) {
                profitByCrop.put(entry.get("crop").asText(), entry);
            }
        }
    }

    public FarmAnalysisResponse getAnalysis(String crop) {
        String key = crop.trim().toLowerCase();
        FarmAnalysisResponse response = new FarmAnalysisResponse();
        response.setCrop(key);

        JsonNode apy = apyByCrop.get(key);
        if (apy != null) {
            response.setHistoricalDataAvailable(true);
            response.setTypicalSeason(apy.get("typicalSeason").asText());
            response.setAvgAreaHectare(apy.get("avgAreaHectare").asDouble());
            response.setAvgYieldTonPerHectareApy(apy.get("avgYieldTonPerHectare").asDouble());
            response.setApyRecordCount(apy.get("recordCount").asInt());
        } else {
            response.setHistoricalDataAvailable(false);
        }

        JsonNode profit = profitByCrop.get(key);
        if (profit != null) {
            response.setProfitDataAvailable(true);
            response.setAvgYieldTonPerHectareProfit(profit.get("avgYieldTonPerHectare").asDouble());
            response.setAvgSeedCostInr(profit.get("avgSeedCostINR").asDouble());
            response.setAvgFertilizerCostInr(profit.get("avgFertilizerCostINR").asDouble());
            response.setAvgPesticideCostInr(profit.get("avgPesticideCostINR").asDouble());
            response.setAvgLaborCostInr(profit.get("avgLaborCostINR").asDouble());
            response.setAvgIrrigationCostInr(profit.get("avgIrrigationCostINR").asDouble());
            response.setAvgTotalCostInr(profit.get("avgTotalCostINR").asDouble());
            response.setAvgRevenueInr(profit.get("avgRevenueINR").asDouble());
            response.setAvgNetProfitInr(profit.get("avgNetProfitINR").asDouble());
            response.setAvgRoiPercent(profit.get("avgROIPercent").asDouble());
        } else {
            response.setProfitDataAvailable(false);
        }

        return response;
    }

    /** Used by CropPredictionService to attach a lightweight snapshot to a fresh prediction. */
    public boolean hasProfitData(String crop) {
        return profitByCrop.containsKey(crop.trim().toLowerCase());
    }

    public boolean hasHistoricalData(String crop) {
        return apyByCrop.containsKey(crop.trim().toLowerCase());
    }

    public JsonNode getRawApy(String crop) {
        return apyByCrop.get(crop.trim().toLowerCase());
    }

    public JsonNode getRawProfit(String crop) {
        return profitByCrop.get(crop.trim().toLowerCase());
    }
}
