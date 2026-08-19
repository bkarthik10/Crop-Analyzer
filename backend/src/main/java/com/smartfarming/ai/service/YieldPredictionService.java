package com.smartfarming.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfarming.ai.dto.YieldPredictionRequest;
import com.smartfarming.ai.dto.YieldPredictionResponse;
import com.smartfarming.ai.model.YieldModel;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Satellite-index yield estimate. Not wired to the main Recommendation form
 * (which doesn't collect NDVI/GNDVI/etc.) — exposed as its own capability for
 * a future field-imagery integration. Reports real R2/MAE from the field-based
 * validation split, never a fabricated "accuracy %".
 */
@Service
public class YieldPredictionService {

    private final YieldModel yieldModel;
    private double r2;
    private double mae;

    public YieldPredictionService(YieldModel yieldModel) {
        this.yieldModel = yieldModel;
    }

    @PostConstruct
    public void loadMetrics() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource("models/yield-model/yield_metrics.json").getInputStream()) {
            JsonNode node = mapper.readTree(is);
            r2 = node.get("r2").asDouble();
            mae = node.get("mae").asDouble();
        }
    }

    public YieldPredictionResponse predict(YieldPredictionRequest request) {
        double[] x = {
                request.getNdvi(), request.getGndvi(), request.getNdwi(), request.getSavi(),
                request.getSoilMoisture(), request.getTemperature(), request.getRainfall()
        };
        double predicted = yieldModel.predict(x);
        return new YieldPredictionResponse(Math.round(predicted * 100.0) / 100.0, r2, mae);
    }
}
