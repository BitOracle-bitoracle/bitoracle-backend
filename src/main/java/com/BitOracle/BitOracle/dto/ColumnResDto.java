package com.BitOracle.BitOracle.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

//전체 게시글 조회 응답 dto
@Getter
@Builder
public class ColumnResDto {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime createdAt;
    private String thumbnailUrl; // 🔸 칼럼글용 썸네일 필드
}
