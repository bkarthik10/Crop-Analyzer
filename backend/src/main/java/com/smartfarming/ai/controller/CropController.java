package com.smartfarming.ai.controller;

import com.smartfarming.ai.dto.CropPredictionRequest;
import com.smartfarming.ai.dto.CropPredictionResponse;
import com.smartfarming.ai.service.CropPredictionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crop")
public class CropController {

    private final CropPredictionService cropPredictionService;

    public CropController(CropPredictionService cropPredictionService) {
        this.cropPredictionService = cropPredictionService;
    }

    /**
     * The core feature: N/P/K/pH/rainfall/temperature in, best-fit crop out
     * (Random Forest, 94.00% held-out accuracy), enriched with soil health,
     * a real historical yield/profit snapshot when available, and an image.
     */
    @PostMapping("/predict")
    public CropPredictionResponse predict(@Valid @RequestBody CropPredictionRequest request) {
        return cropPredictionService.predict(request);
    }
}
