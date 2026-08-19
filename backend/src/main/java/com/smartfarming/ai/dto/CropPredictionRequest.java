package com.smartfarming.ai.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input for POST /api/crop/predict.
 *
 * <p>Deliberately six fields, not seven: the crop dataset this model was
 * trained on (Train_Dataset.csv) has no humidity column, so — unlike the
 * original UI mockup — this request has no humidity field. Humidity is used
 * on the Fertilizer step instead, where the dataset actually has it.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropPredictionRequest {

    @NotNull
    @DecimalMin(value = "0.0", message = "Nitrogen must be 0 or greater")
    @DecimalMax(value = "300.0", message = "Nitrogen must be 300 or less")
    private Double nitrogen;

    @NotNull
    @DecimalMin(value = "0.0", message = "Phosphorus must be 0 or greater")
    @DecimalMax(value = "200.0", message = "Phosphorus must be 200 or less")
    private Double phosphorus;

    @NotNull
    @DecimalMin(value = "0.0", message = "Potassium must be 0 or greater")
    @DecimalMax(value = "300.0", message = "Potassium must be 300 or less")
    private Double potassium;

    @NotNull
    @DecimalMin(value = "0.0", message = "pH must be 0 or greater")
    @DecimalMax(value = "14.0", message = "pH must be 14 or less")
    private Double ph;

    @NotNull
    @DecimalMin(value = "0.0", message = "Rainfall must be 0 or greater")
    @DecimalMax(value = "5000.0", message = "Rainfall must be 5000 or less")
    private Double rainfall;

    @NotNull
    @DecimalMin(value = "-10.0", message = "Temperature must be -10 or greater")
    @DecimalMax(value = "60.0", message = "Temperature must be 60 or less")
    private Double temperature;
}
