-- Crop Analyzer / Smart Farming AI Platform
-- MySQL Database Schema

CREATE DATABASE IF NOT EXISTS crop_analyzer;
USE crop_analyzer;

-- Prediction History: logged when a farmer runs a crop recommendation
CREATE TABLE prediction_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    predicted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    nitrogen DOUBLE NOT NULL,
    phosphorus DOUBLE NOT NULL,
    potassium DOUBLE NOT NULL,
    ph DOUBLE NOT NULL,
    rainfall DOUBLE NOT NULL,
    temperature DOUBLE NOT NULL,
    predicted_crop VARCHAR(100) NOT NULL,
    confidence DOUBLE NOT NULL,
    INDEX idx_predicted_at (predicted_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample data (optional)
INSERT INTO prediction_history 
(nitrogen, phosphorus, potassium, ph, rainfall, temperature, predicted_crop, confidence) 
VALUES 
(50, 50, 50, 6.5, 100, 25, 'Rice', 0.94),
(60, 40, 40, 6.2, 120, 20, 'Wheat', 0.92),
(45, 35, 55, 6.8, 90, 28, 'Maize', 0.91);
