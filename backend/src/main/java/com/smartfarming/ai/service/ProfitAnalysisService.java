package com.smartfarming.ai.service;

import org.springframework.stereotype.Service;

/**
 * Ad-hoc profit calculator: given a farmer's own area/yield/price/cost figures
 * (rather than the dataset averages {@link FarmAnalysisService} serves), works
 * out revenue, net profit and ROI. Formulas verified directly against
 * farm_profit_dashboard_dataset.csv during data exploration — Total_Cost_INR
 * and Net_Profit_INR in that dataset match these formulas to floating-point
 * precision, so this isn't a guessed calculation.
 */
@Service
public class ProfitAnalysisService {

    public record ProfitResult(double production, double revenue, double totalCost, double netProfit, double roiPercent) {}

    /**
     * @param areaHectare   cultivated area
     * @param yieldTonPerHa expected yield per hectare
     * @param marketPriceInrPerTon market price per ton
     * @param totalCostInr  total cost already known/estimated by the farmer
     */
    public ProfitResult calculate(double areaHectare, double yieldTonPerHa, double marketPriceInrPerTon, double totalCostInr) {
        double production = areaHectare * yieldTonPerHa;
        double revenue = production * marketPriceInrPerTon;
        double netProfit = revenue - totalCostInr;
        double roi = totalCostInr > 0 ? (netProfit / totalCostInr) * 100 : 0;
        return new ProfitResult(round2(production), round2(revenue), round2(totalCostInr), round2(netProfit), round2(roi));
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
