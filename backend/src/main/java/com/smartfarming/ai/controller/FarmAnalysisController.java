package com.smartfarming.ai.controller;

import com.smartfarming.ai.dto.FarmAnalysisResponse;
import com.smartfarming.ai.dto.YieldPredictionRequest;
import com.smartfarming.ai.dto.YieldPredictionResponse;
import com.smartfarming.ai.service.FarmAnalysisService;
import com.smartfarming.ai.service.ProfitAnalysisService;
import com.smartfarming.ai.service.YieldPredictionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farm-analysis")
public class FarmAnalysisController {

    private final FarmAnalysisService farmAnalysisService;
    private final ProfitAnalysisService profitAnalysisService;
    private final YieldPredictionService yieldPredictionService;

    public FarmAnalysisController(FarmAnalysisService farmAnalysisService,
                                   ProfitAnalysisService profitAnalysisService,
                                   YieldPredictionService yieldPredictionService) {
        this.farmAnalysisService = farmAnalysisService;
        this.profitAnalysisService = profitAnalysisService;
        this.yieldPredictionService = yieldPredictionService;
    }

    /** Real historical APY + farm-profit averages for a crop, where available. */
    @GetMapping("/{crop}")
    public FarmAnalysisResponse analyze(@PathVariable String crop) {
        return farmAnalysisService.getAnalysis(crop);
    }

    /** Ad-hoc calculator using the farmer's own numbers rather than dataset averages. */
    @PostMapping("/profit-calculator")
    public ProfitAnalysisService.ProfitResult calculateProfit(@RequestBody ProfitCalculatorRequest request) {
        return profitAnalysisService.calculate(request.areaHectare(), request.yieldTonPerHectare(),
                request.marketPriceInrPerTon(), request.totalCostInr());
    }

    /** Satellite-index (NDVI/GNDVI/NDWI/SAVI) yield estimate — see YieldPredictionService for scope notes. */
    @PostMapping("/yield-estimate")
    public YieldPredictionResponse estimateYield(@Valid @RequestBody YieldPredictionRequest request) {
        return yieldPredictionService.predict(request);
    }

    public record ProfitCalculatorRequest(
            @NotNull Double areaHectare,
            @NotNull Double yieldTonPerHectare,
            @NotNull Double marketPriceInrPerTon,
            @NotNull Double totalCostInr) {
    }
}
