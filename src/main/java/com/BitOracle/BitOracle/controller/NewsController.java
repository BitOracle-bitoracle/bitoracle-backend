package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.dto.NewsResponseDto;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/news/goodNews")
    public DataResponseDto<Page<NewsResponseDto.GoodBadNewsResponseDto>> getGoodNews(
            @RequestParam(defaultValue = "0",name = "page")int page,
            @RequestParam(defaultValue = "5",name = "size") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NewsResponseDto.GoodBadNewsResponseDto> responseDto = newsService.getGoodNews(pageable);
        return DataResponseDto.of(responseDto, "호재 뉴스를 페이지로 조회했습니다.");
    }

    @GetMapping("/news/badNews")
    public DataResponseDto<Page<NewsResponseDto.GoodBadNewsResponseDto>> getBadNews(
            @RequestParam(defaultValue = "0",name = "page")int page,
            @RequestParam(defaultValue = "5",name = "size") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NewsResponseDto.GoodBadNewsResponseDto> responseDto = newsService.getBadNews(pageable);
        return DataResponseDto.of(responseDto, "악재 뉴스를 페이지로 조회했습니다.");
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
