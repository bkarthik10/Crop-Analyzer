package com.smartfarming.ai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input for POST /api/farm-analysis/yield-estimate.
 * Separate from CropPredictionRequest because this model uses satellite
 * vegetation indices, not soil NPK/pH — a genuinely different feature set.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YieldPredictionRequest {

    @NotNull private Double ndvi;
    @NotNull private Double gndvi;
    @NotNull private Double ndwi;
    @NotNull private Double savi;
    @NotNull private Double soilMoisture;
    @NotNull private Double temperature;
    @NotNull private Double rainfall;
}
