package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.dto.NewsResponseDto;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class NewsController {

    private final NewsService newsService;

    @GetMapping(value = "/news/main")
    public DataResponseDto<List<NewsResponseDto.MainNewsResponseDto>> getMainNews(){
        List<NewsResponseDto.MainNewsResponseDto> responseDto = newsService.getMainNews();
        return DataResponseDto.of(responseDto, "오늘의 뉴스 6개를 조회했습니다.");
    }

    @GetMapping(value = "/news/goodNews")
    public DataResponseDto<List<NewsResponseDto.GoodBadNewsResponseDto>> getGoodNews(){
        List<NewsResponseDto.GoodBadNewsResponseDto> responseDto = newsService.getGoodNews();
        return DataResponseDto.of(responseDto, "호재 뉴스를 모두 조회했습니다.");
    }

    @GetMapping(value = "/news/badNews")
    public DataResponseDto<List<NewsResponseDto.GoodBadNewsResponseDto>> getBadNews(){
        List<NewsResponseDto.GoodBadNewsResponseDto> responseDto = newsService.getBadNews();
        return DataResponseDto.of(responseDto, "악재 뉴스를 모두 조회했습니다.");
    }

    @GetMapping(value = "/news/kw")
    public DataResponseDto<NewsResponseDto.kwResponseDto> getKeyword(){
        String kw = newsService.getKeyword();
        NewsResponseDto.kwResponseDto responseDto = NewsResponseDto.kwResponseDto.builder()
                .kw(kw)
                .build();
        return DataResponseDto.of(responseDto, "오늘의 키워드를 조회했습니다.");
    }
}
