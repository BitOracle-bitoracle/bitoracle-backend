package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
}
