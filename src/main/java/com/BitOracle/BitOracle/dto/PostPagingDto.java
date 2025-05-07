package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.domain.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PostPagingDto {
    private int totalPageCount;//총페이지
    private int currentPageNum;//현재 몇페이지
    private long totalElementCount; //존재하는 게시글의 총 개수
    private int currentPageElementCount;// 현재 페이지에 존재하는 게시글 수

    private List<PostResDto> simpleLectureDtoList = new ArrayList<>();

    public PostPagingDto(Page<Post> searchResults) {
        this.totalPageCount = searchResults.getTotalPages();
        this.currentPageNum = searchResults.getNumber();
        this.totalElementCount = searchResults.getTotalElements();
        this.currentPageElementCount = searchResults.getNumberOfElements();
        this.simpleLectureDtoList = searchResults.getContent().stream()
                .map(post -> PostResDto.builder()
                        .id(post.getPostId())
                        .content(post.getContent())
                        .title(post.getTitle())
                        .writer(post.getUser().getNickname())
                        .createdAt(post.getCreatedAt())
                        .build())
                .toList();
    }
}
