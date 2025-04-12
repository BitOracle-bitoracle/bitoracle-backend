package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.domain.enums.NewsType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawledNewsDto {
    private String title;
    private String url;
    private String summary;
    private String imageUrl;
    private NewsType newsType;
}
