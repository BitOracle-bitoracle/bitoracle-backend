package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.dto.NewsResponseDto;
import com.BitOracle.BitOracle.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public List<NewsResponseDto.MainNewsResponseDto> getMainNews(){
        return newsRepository.findTop6ByOrderByCreatedAtDesc().stream()
                .map(news -> new NewsResponseDto.MainNewsResponseDto(
                        news.getNewsTitle(),
                        news.getNewsContent().substring(0, 50) + "...", // 지금은 50자만 응답하는걸로. 화면 나오는거 보고 조절
                        news.getImageUrl(),
                        news.getNewsType()
                ))
                .toList();
    }
}
