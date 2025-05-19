package com.BitOracle.BitOracle.converter;

import com.BitOracle.BitOracle.domain.News;
import com.BitOracle.BitOracle.dto.NewsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NewsConverter {

    public List<NewsResponseDto.MainNewsResponseDto> toMainNewsResponseDto(List<News> newsList) {
        return newsList.stream()
                .map(news -> NewsResponseDto.MainNewsResponseDto.builder()
                        .newsTitle(news.getNewsTitle())
                        .newsContent(news.getNewsContent().substring(0, 50) + "...")
                        .imageUrl(news.getImageUrl())
                        .newsType(news.getNewsType())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public NewsResponseDto.GoodBadNewsResponseDto toGoodBadNewsResponseDto(News news) {
        return NewsResponseDto.GoodBadNewsResponseDto.builder()
                .newsTitle(news.getNewsTitle())
                .newsContent(news.getNewsTitle())
                .newsUrl(news.getNewsUrl())
                .createdAt(news.getCreatedAt())
                .build();
    }
}
