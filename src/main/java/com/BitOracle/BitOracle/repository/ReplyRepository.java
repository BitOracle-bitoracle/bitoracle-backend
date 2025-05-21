package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    void deleteAllByPost(Post post);

    List<Reply> findByPost(Post post);
}
