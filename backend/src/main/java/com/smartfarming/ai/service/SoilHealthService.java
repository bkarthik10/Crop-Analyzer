package com.smartfarming.ai.service;

import com.smartfarming.ai.util.CsvReader;
import com.smartfarming.ai.util.DataProcessor;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Computes soil nutrient status (Low / Optimal / High) and an overall soil
 * health score by comparing measured N/P/K/pH against the actual 25th-75th
 * percentile range observed for that crop in the training data — not fixed,
 * guessed thresholds. Ranges are computed once at startup from crop.csv.
 */
@Service
public class SoilHealthService {

    private final CsvReader csvReader;
    private final DataProcessor dataProcessor;

    /** crop -> {N: [p25, p75], P: [...], K: [...], pH: [...]} */
    private final Map<String, Map<String, double[]>> optimalRanges = new HashMap<>();

    public SoilHealthService(CsvReader csvReader, DataProcessor dataProcessor) {
        this.csvReader = csvReader;
        this.dataProcessor = dataProcessor;
    }

    @PostConstruct
    public void computeRanges() throws IOException {
        List<CSVRecord> records = csvReader.readClasspathCsv("datasets/crop.csv");

        Map<String, List<Double>> nByCrop = new HashMap<>();
        Map<String, List<Double>> pByCrop = new HashMap<>();
        Map<String, List<Double>> kByCrop = new HashMap<>();
        Map<String, List<Double>> phByCrop = new HashMap<>();

        for (CSVRecord r : records) {
            String crop = r.get("Crop");
            nByCrop.computeIfAbsent(crop, c -> new ArrayList<>()).add(dataProcessor.parseDoubleSafe(r.get("N"), 0));
            pByCrop.computeIfAbsent(crop, c -> new ArrayList<>()).add(dataProcessor.parseDoubleSafe(r.get("P"), 0));
            kByCrop.computeIfAbsent(crop, c -> new ArrayList<>()).add(dataProcessor.parseDoubleSafe(r.get("K"), 0));
            phByCrop.computeIfAbsent(crop, c -> new ArrayList<>()).add(dataProcessor.parseDoubleSafe(r.get("pH"), 0));
        }

        for (String crop : nByCrop.keySet()) {
            Map<String, double[]> ranges = new HashMap<>();
            ranges.put("N", percentileRange(nByCrop.get(crop)));
            ranges.put("P", percentileRange(pByCrop.get(crop)));
            ranges.put("K", percentileRange(kByCrop.get(crop)));
            ranges.put("pH", percentileRange(phByCrop.get(crop)));
            optimalRanges.put(crop, ranges);
        }
    }

    private double[] percentileRange(List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        double p25 = percentile(sorted, 25);
        double p75 = percentile(sorted, 75);
        return new double[]{p25, p75};
    }

    private double percentile(List<Double> sorted, double pct) {
        if (sorted.isEmpty()) return 0;
        double index = (pct / 100.0) * (sorted.size() - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        if (lower == upper) return sorted.get(lower);
        double fraction = index - lower;
        return sorted.get(lower) + fraction * (sorted.get(upper) - sorted.get(lower));
    }

    public record SoilHealthResult(int score, Map<String, String> nutrientStatus) {}

    /**
     * Scores measured N/P/K/pH against the given crop's real optimal range.
     * Falls back to the dataset-wide range if the crop is unrecognized.
     */
    public SoilHealthResult evaluate(String crop, double n, double p, double k, double ph) {
        Map<String, double[]> ranges = optimalRanges.getOrDefault(dataProcessor.normalize(crop), null);
        if (ranges == null) {
            // try case-sensitive-insensitive lookup against stored keys (crop.csv stores lowercase already)
            ranges = optimalRanges.get(crop.trim().toLowerCase());
        }
        if (ranges == null) {
            ranges = datasetWideRange();
        }

        Map<String, String> status = new LinkedHashMap<>();
        status.put("Nitrogen", classify(n, ranges.get("N")));
        status.put("Phosphorus", classify(p, ranges.get("P")));
        status.put("Potassium", classify(k, ranges.get("K")));
        status.put("pH", classify(ph, ranges.get("pH")));

        int optimalCount = (int) status.values().stream().filter(s -> s.equals("Optimal")).count();
        int score = (int) Math.round(40 + (optimalCount / 4.0) * 60); // 40 floor, up to 100 when all 4 are optimal

        return new SoilHealthResult(score, status);
    }

    private String classify(double value, double[] range) {
        if (range == null) return "Optimal";
        if (value < range[0]) return "Low";
        if (value > range[1]) return "High";
        return "Optimal";
    }

    private Map<String, double[]> datasetWideRange() {
        // Reasonable overall fallback spanning the full dataset's typical band (see /ml/model-results for source stats)
        Map<String, double[]> fallback = new HashMap<>();
        fallback.put("N", new double[]{50, 100});
        fallback.put("P", new double[]{35, 60});
        fallback.put("K", new double[]{20, 50});
        fallback.put("pH", new double[]{5.3, 5.7});
        return fallback;
    }

    public Map<String, double[]> getOptimalRangeForCrop(String crop) {
        return optimalRanges.get(crop.trim().toLowerCase());
    }
}
