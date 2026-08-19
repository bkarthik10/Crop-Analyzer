package com.smartfarming.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfarming.ai.dto.FertilizerRequest;
import com.smartfarming.ai.dto.FertilizerResponse;
import com.smartfarming.ai.model.FertilizerModel;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Thin orchestration layer over {@link FertilizerModel}'s rule engine, plus
 * the reference category lists (soil types / crop types) the frontend's
 * dropdowns are populated from — these are the real categories present in
 * Fertilizer_Prediction.csv, not a hand-typed guess.
 */
@Service
public class FertilizerService {

    private final FertilizerModel fertilizerModel;

    private List<String> soilTypes = new ArrayList<>();
    private List<String> cropTypes = new ArrayList<>();
    private List<String> fertilizerNames = new ArrayList<>();

    public FertilizerService(FertilizerModel fertilizerModel) {
        this.fertilizerModel = fertilizerModel;
    }

    @PostConstruct
    public void loadOptions() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource("datasets/fertilizer_classes.json").getInputStream()) {
            JsonNode root = mapper.readTree(is);
            root.get("soilTypes").forEach(n -> soilTypes.add(n.asText()));
            root.get("cropTypes").forEach(n -> cropTypes.add(n.asText()));
            root.get("fertilizerNames").forEach(n -> fertilizerNames.add(n.asText()));
        }
    }

    public FertilizerResponse recommend(FertilizerRequest request) {
        FertilizerModel.Recommendation rec = fertilizerModel.recommend(
                request.getNitrogen(), request.getPhosphorous(), request.getPotassium());

        return new FertilizerResponse(
                rec.fertilizerName(),
                rec.reasoning(),
                rec.usageNote(),
                "rule-based agronomic engine (see FertilizerModel.java — the raw dataset showed no learnable ML signal)"
        );
    }

    public List<String> getSoilTypes() {
        return soilTypes;
    }

    public List<String> getCropTypes() {
        return cropTypes;
    }

    public List<String> getFertilizerNames() {
        return fertilizerNames;
    }
}
