package com.smartfarming.ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A single logged crop prediction — one row per "Predict Best Crop" click.
 * Backs the History page.
 */
@Entity
@Table(name = "prediction_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime predictedAt;

    @Column(nullable = false)
    private Double nitrogen;

    @Column(nullable = false)
    private Double phosphorus;

    @Column(nullable = false)
    private Double potassium;

    @Column(nullable = false)
    private Double ph;

    @Column(nullable = false)
    private Double rainfall;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private String predictedCrop;

    @Column(nullable = false)
    private Double confidence;

    public PredictionHistory(Double nitrogen, Double phosphorus, Double potassium, Double ph,
                              Double rainfall, Double temperature, String predictedCrop, Double confidence) {
        this.predictedAt = LocalDateTime.now();
        this.nitrogen = nitrogen;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.ph = ph;
        this.rainfall = rainfall;
        this.temperature = temperature;
        this.predictedCrop = predictedCrop;
        this.confidence = confidence;
    }
}
