package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.News;
import com.BitOracle.BitOracle.domain.enums.NewsType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByNewsUrl(String newsUrl);
    List<News> findTop6ByOrderByCreatedAtDesc();
    Page<News> findByNewsType(NewsType newsType, Pageable pageable);

}
