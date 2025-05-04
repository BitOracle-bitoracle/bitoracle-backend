package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
    UserEntity findByName(String name);
}
