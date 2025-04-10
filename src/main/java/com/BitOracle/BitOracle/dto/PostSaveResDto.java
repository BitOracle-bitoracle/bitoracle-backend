package com.BitOracle.BitOracle.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostSaveResDto {
    private Long id;
    private String title;
    private String content;
    private String authorName;
}
