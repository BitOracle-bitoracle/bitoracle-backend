package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
