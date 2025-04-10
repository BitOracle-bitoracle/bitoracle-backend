package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
