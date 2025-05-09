package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.Record;
import com.BitOracle.BitOracle.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Record findByUser(User user);
}
