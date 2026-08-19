package com.smartfarming.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Crop Analyzer / Smart Farming AI Platform.
 *
 * On boot, {@code CropPredictionService} and {@code YieldPredictionService} load the
 * pre-trained Random Forest models (JSON, exported from the real Python/scikit-learn
 * training run — see /ml/notebooks) from the classpath, falling back to training a
 * fresh model from the bundled CSV if no model file is found. See README.md for the
 * reasoning behind this "train in Python, serve in Java" split.
 */
@SpringBootApplication
public class SmartFarmingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartFarmingApplication.class, args);
    }
}
