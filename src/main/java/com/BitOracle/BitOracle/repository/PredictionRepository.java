package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.Prediction;
import com.BitOracle.BitOracle.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Prediction> findByUser(User user);
}
