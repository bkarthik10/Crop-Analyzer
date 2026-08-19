package com.smartfarming.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfarming.ai.dto.CropPredictionRequest;
import com.smartfarming.ai.dto.CropPredictionResponse;
import com.smartfarming.ai.entity.PredictionHistory;
import com.smartfarming.ai.model.CropModel;
import com.smartfarming.ai.repository.PredictionHistoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Orchestrates a crop recommendation request: runs the Random Forest model,
 * scores soil health against real per-crop ranges, attaches whatever
 * historical yield/profit data exists for the predicted crop, resolves an
 * image, and logs the prediction to MySQL for the History page.
 */
@Service
public class CropPredictionService {

    private final CropModel cropModel;
    private final SoilHealthService soilHealthService;
    private final FarmAnalysisService farmAnalysisService;
    private final PredictionHistoryRepository historyRepository;

    private final Map<String, String> cropImages = new HashMap<>();
    private static final String FALLBACK_IMAGE = null; // frontend renders a styled placeholder when this is null

    public CropPredictionService(CropModel cropModel,
                                  SoilHealthService soilHealthService,
                                  FarmAnalysisService farmAnalysisService,
                                  PredictionHistoryRepository historyRepository) {
        this.cropModel = cropModel;
        this.soilHealthService = soilHealthService;
        this.farmAnalysisService = farmAnalysisService;
        this.historyRepository = historyRepository;
    }

    @PostConstruct
    public void loadImages() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource("datasets/crop_images.json").getInputStream()) {
            JsonNode root = mapper.readTree(is);
            root.fieldNames().forEachRemaining(crop -> cropImages.put(crop, root.get(crop).asText()));
        }
    }

    public CropPredictionResponse predict(CropPredictionRequest request) {
        double[] x = {
                request.getNitrogen(), request.getPhosphorus(), request.getPotassium(),
                request.getPh(), request.getRainfall(), request.getTemperature()
        };

        CropModel.Prediction prediction = cropModel.predict(x);
        String crop = prediction.crop();

        CropPredictionResponse response = new CropPredictionResponse();
        response.setCrop(capitalize(crop));
        response.setConfidence(round(prediction.confidence(), 4));
        response.setImageUrl(cropImages.getOrDefault(crop, FALLBACK_IMAGE));

        response.setAlternatives(prediction.topAlternatives().stream()
                .map(alt -> new CropPredictionResponse.AlternativeCrop(capitalize(alt.crop()), round(alt.probability(), 4)))
                .toList());

        SoilHealthService.SoilHealthResult soilHealth = soilHealthService.evaluate(
                crop, request.getNitrogen(), request.getPhosphorus(), request.getPotassium(), request.getPh());
        response.setSoilHealth(new CropPredictionResponse.SoilHealth(soilHealth.score(), soilHealth.nutrientStatus()));

        if (farmAnalysisService.hasHistoricalData(crop) || farmAnalysisService.hasProfitData(crop)) {
            var apy = farmAnalysisService.getRawApy(crop);
            var profit = farmAnalysisService.getRawProfit(crop);
            CropPredictionResponse.FarmSnapshot snapshot = new CropPredictionResponse.FarmSnapshot();
            if (apy != null) {
                snapshot.setTypicalSeason(apy.get("typicalSeason").asText());
                snapshot.setAvgYieldTonPerHectare(apy.get("avgYieldTonPerHectare").asDouble());
                snapshot.setAvgAreaHectare(apy.get("avgAreaHectare").asDouble());
            }
            if (profit != null) {
                snapshot.setAvgNetProfitInr(profit.get("avgNetProfitINR").asDouble());
                snapshot.setAvgRoiPercent(profit.get("avgROIPercent").asDouble());
            }
            response.setFarmSnapshot(snapshot);
        }

        historyRepository.save(new PredictionHistory(
                request.getNitrogen(), request.getPhosphorus(), request.getPotassium(), request.getPh(),
                request.getRainfall(), request.getTemperature(), response.getCrop(), response.getConfidence()));

        return response;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private double round(double v, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(v * factor) / factor;
    }
}
