package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepoistory extends JpaRepository<Coin, Long> {
}
