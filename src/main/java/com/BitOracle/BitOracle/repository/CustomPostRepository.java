package com.BitOracle.BitOracle.repository;

import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.dummy.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPostRepository {
    Page<Post> search(PostSearchCondition postSearchCondition, Pageable pageable);
}
