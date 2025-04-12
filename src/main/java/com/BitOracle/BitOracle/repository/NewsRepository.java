package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByNewsUrl(String newsUrl);
}
