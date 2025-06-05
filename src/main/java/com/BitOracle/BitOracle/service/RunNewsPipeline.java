package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.News;
import com.BitOracle.BitOracle.domain.enums.NewsType;
import com.BitOracle.BitOracle.dto.CrawledNewsDto;
import com.BitOracle.BitOracle.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunNewsPipeline {

    private final DeepSearchNewsCrawler crawler;
    private final OpenAiService openAiService;
    private final NewsRepository newsRepository;

    @Scheduled(cron = "0 0 10 * * *") // 매일 01시 실행 (인스턴스 시차 때문에 이렇게)
    public void runNewsPipeline() {
        List<CrawledNewsDto> allNews = crawler.fetchNews();

        List<News> bullish = new ArrayList<>();
        List<News> bearish = new ArrayList<>();

        for (CrawledNewsDto dto : allNews) {
            // 이미 있는 URL이면 패스
            if (newsRepository.existsByNewsUrl(dto.getUrl())) {
                log.info("이미 저장된 뉴스입니다: {}", dto.getUrl());
                continue;
            }

            NewsType type = openAiService.classifyNews(dto.getTitle(), dto.getSummary());

            // ✂ 요약 내용이 너무 길면 자르기
            String trimmedSummary = dto.getSummary();
            if (trimmedSummary.length() > 255) {
                trimmedSummary = trimmedSummary.substring(0, 255);
            }

            News news = News.builder()
                    .newsTitle(dto.getTitle())
                    .newsUrl(dto.getUrl())
                    .newsContent(trimmedSummary)
                    .imageUrl(dto.getImageUrl())
                    .newsType(type)
                    .build();

            if (type == NewsType.GOOD && bullish.size() < 3) bullish.add(news);
            if (type == NewsType.BAD && bearish.size() < 3) bearish.add(news);

            if (bullish.size() == 3 && bearish.size() == 3) break;
        }

        newsRepository.saveAll(bullish);
        newsRepository.saveAll(bearish);
        log.info("뉴스 저장 완료. 호재: {}, 악재: {}", bullish.size(), bearish.size());
    }
}
