package com.smartfarming.ai.repository;

import com.smartfarming.ai.entity.PredictionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionHistoryRepository extends JpaRepository<PredictionHistory, Long> {

    /** Most recent predictions first — what the History page shows. */
    List<PredictionHistory> findTop50ByOrderByPredictedAtDesc();
}
