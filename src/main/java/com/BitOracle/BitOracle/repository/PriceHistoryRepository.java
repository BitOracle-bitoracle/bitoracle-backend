package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.Prediction;
import com.BitOracle.BitOracle.domain.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    Optional<PriceHistory> findByDate(LocalDate date);
    boolean existsByDate(LocalDate date);
    List<PriceHistory> findAllByOrderByDateAsc(); // 날짜 오름차순 정렬
}
