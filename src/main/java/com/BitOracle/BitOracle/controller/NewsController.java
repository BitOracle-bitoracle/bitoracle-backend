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
}
