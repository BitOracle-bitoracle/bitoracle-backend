package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.PredictedHistory;
import com.BitOracle.BitOracle.domain.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PredictedHistoryRepository extends JpaRepository<PredictedHistory, Long> {
    Optional<PredictedHistory> findByDate(LocalDate date);
    boolean existsByDate(LocalDate date);
    List<PredictedHistory> findAllByOrderByDateAsc();
}
