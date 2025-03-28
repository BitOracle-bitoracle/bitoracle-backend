package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.BtcPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BtcPriceRepository extends JpaRepository<BtcPrice, Long> {
    BtcPrice findByCoinDate(LocalDate date);
}
