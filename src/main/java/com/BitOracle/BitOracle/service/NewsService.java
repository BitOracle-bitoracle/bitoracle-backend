package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.converter.NewsConverter;
import com.BitOracle.BitOracle.domain.News;
import com.BitOracle.BitOracle.domain.enums.NewsType;
import com.BitOracle.BitOracle.dto.NewsResponseDto;
import com.BitOracle.BitOracle.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsConverter newsConverter;

    public List<NewsResponseDto.MainNewsResponseDto> getMainNews(){
        List<News> mainNewsList = newsRepository.findTop6ByOrderByCreatedAtDesc();
        return newsConverter.toMainNewsResponseDto(mainNewsList);
    }

    public List<NewsResponseDto.GoodBadNewsResponseDto> getGoodNews() {
        List<News> goodNewsList = newsRepository.findByNewsType(NewsType.GOOD);
        return newsConverter.toGoodBadNewsResponseDtoList(goodNewsList);
    }

    public List<NewsResponseDto.GoodBadNewsResponseDto> getBadNews() {
        List<News> badNewsList = newsRepository.findByNewsType(NewsType.BAD);
        return newsConverter.toGoodBadNewsResponseDtoList(badNewsList);
    }
}
