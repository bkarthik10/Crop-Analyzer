package com.smartfarming.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input for POST /api/fertilizer/recommend.
 * Soil Type and Crop Type are free text but should come from the dropdown
 * lists returned by GET /api/fertilizer/options (the real categories present
 * in the dataset) so downstream reference notes can be looked up cleanly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FertilizerRequest {

    @NotNull
    private Double temperature;

    @NotNull
    private Double humidity;

    @NotNull
    private Double moisture;

    @NotBlank
    private String soilType;

    @NotBlank
    private String cropType;

    @NotNull
    private Double nitrogen;

    @NotNull
    private Double potassium;

    @NotNull
    private Double phosphorous;
}
