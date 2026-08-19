package com.smartfarming.ai.controller;

import com.smartfarming.ai.dto.FertilizerRequest;
import com.smartfarming.ai.dto.FertilizerResponse;
import com.smartfarming.ai.service.FertilizerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fertilizer")
public class FertilizerController {

    private final FertilizerService fertilizerService;

    public FertilizerController(FertilizerService fertilizerService) {
        this.fertilizerService = fertilizerService;
    }

    @PostMapping("/recommend")
    public FertilizerResponse recommend(@Valid @RequestBody FertilizerRequest request) {
        return fertilizerService.recommend(request);
    }

    /** Real category lists from the dataset, for the form's dropdowns. */
    @GetMapping("/options")
    public Map<String, List<String>> options() {
        return Map.of(
                "soilTypes", fertilizerService.getSoilTypes(),
                "cropTypes", fertilizerService.getCropTypes(),
                "fertilizerNames", fertilizerService.getFertilizerNames()
        );
    }
}
