package com.smartfarming.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Output of GET /api/farm-analysis/{crop}. Any field can be null when the
 * crop isn't covered by that particular dataset (APY tracks ~23 of the 40
 * crop-model crops; the profit dataset only tracks 6) — the frontend shows
 * "not available" rather than a fabricated number in that case.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmAnalysisResponse {

    private String crop;

    // From APY.csv aggregates (historical government crop data)
    private String typicalSeason;
    private Double avgAreaHectare;
    private Double avgYieldTonPerHectareApy;
    private Integer apyRecordCount;

    // From farm_profit_dashboard_dataset.csv aggregates (only Cotton, Maize, Potato, Rice, Sugarcane, Wheat)
    private Double avgYieldTonPerHectareProfit;
    private Double avgSeedCostInr;
    private Double avgFertilizerCostInr;
    private Double avgPesticideCostInr;
    private Double avgLaborCostInr;
    private Double avgIrrigationCostInr;
    private Double avgTotalCostInr;
    private Double avgRevenueInr;
    private Double avgNetProfitInr;
    private Double avgRoiPercent;

    private boolean historicalDataAvailable;
    private boolean profitDataAvailable;
}
