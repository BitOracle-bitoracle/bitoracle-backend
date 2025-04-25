package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.domain.enums.NewsType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NewsResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainNewsResponseDto {
        @JsonProperty("news_title")
        private String newsTitle;
        @JsonProperty("news_content")
        private String newsContent;
        @JsonProperty("image_url")
        private String imageUrl;
        @JsonProperty("news_type")
        private NewsType newsType;
    }
}
