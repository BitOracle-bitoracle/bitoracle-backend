package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refresh);
    Optional<RefreshEntity> findByUsername(String username);

    @Transactional
    void deleteByRefresh(String refresh);
}
