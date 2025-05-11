package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
