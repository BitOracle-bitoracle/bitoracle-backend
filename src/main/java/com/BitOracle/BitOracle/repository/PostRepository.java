package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long>, CustomPostRepository {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Post findByPostId(Long postId);

    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findWithWriterByPostId(Long postId);
}
