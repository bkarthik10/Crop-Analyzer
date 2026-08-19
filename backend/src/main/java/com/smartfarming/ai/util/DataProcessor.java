package com.smartfarming.ai.util;

import org.springframework.stereotype.Component;

/**
 * Small, dependency-free parsing/formatting helpers shared across services.
 * Kept intentionally simple: this is glue code around already-cleaned CSVs,
 * not a general-purpose data cleaning framework.
 */
@Component
public class DataProcessor {

    /** Parses a String to double, returning a fallback instead of throwing on bad input. */
    public double parseDoubleSafe(String value, double fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /** Normalizes free-text category values for comparison (trim + lowercase). */
    public String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    /** Rounds to a fixed number of decimal places without pulling in BigDecimal everywhere. */
    public double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }

    /** Clamps a value into [min, max] — used to guard against wild/garbage inputs. */
    public double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
