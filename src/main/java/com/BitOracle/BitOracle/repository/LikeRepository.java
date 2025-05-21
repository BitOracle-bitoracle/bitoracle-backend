package com.BitOracle.BitOracle.repository;


import com.BitOracle.BitOracle.domain.Likes;
import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes ,Long> {
    boolean existsByUserAndPost(User user, Post post);
    Optional<Likes> findByUserAndPost(User user, Post post);
    long countByPost(Post post);

    void deleteAllByPost(Post post);

}
