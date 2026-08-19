package com.smartfarming.ai.controller;

import com.smartfarming.ai.entity.PredictionHistory;
import com.smartfarming.ai.repository.PredictionHistoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final PredictionHistoryRepository historyRepository;

    public HistoryController(PredictionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /** Most recent 50 crop predictions, newest first — backs the History page. */
    @GetMapping
    public List<PredictionHistory> recent() {
        return historyRepository.findTop50ByOrderByPredictedAtDesc();
    }
}
