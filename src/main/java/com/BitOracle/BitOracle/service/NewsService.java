package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.converter.NewsConverter;
import com.BitOracle.BitOracle.domain.BtcPrice;
import com.BitOracle.BitOracle.domain.News;
import com.BitOracle.BitOracle.domain.enums.NewsType;
import com.BitOracle.BitOracle.dto.NewsResponseDto;
import com.BitOracle.BitOracle.repository.BtcPriceRepository;
import com.BitOracle.BitOracle.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;
    private final BtcPriceRepository btcPriceRepository;
    private final OpenAiService openAiService;
    private final NewsConverter newsConverter;

    public List<NewsResponseDto.MainNewsResponseDto> getMainNews(){
        List<News> mainNewsList = newsRepository.findTop6ByOrderByCreatedAtDesc();
        return newsConverter.toMainNewsResponseDto(mainNewsList);
    }

    public Page<NewsResponseDto.GoodBadNewsResponseDto> getGoodNews(Pageable pageable) {
        Page<News> goodNewsPage = newsRepository.findByNewsType(NewsType.GOOD, pageable);
        return goodNewsPage.map(newsConverter::toGoodBadNewsResponseDto);
    }

    public Page<NewsResponseDto.GoodBadNewsResponseDto> getBadNews(Pageable pageable) {
        Page<News> badNewsPage = newsRepository.findByNewsType(NewsType.BAD, pageable);
        return badNewsPage.map(newsConverter::toGoodBadNewsResponseDto);
    }

    public String getKeyword() {
        List<News> todayNewsList = newsRepository.findTop6ByOrderByCreatedAtDesc();

        LocalDate today = LocalDate.now();
        log.info("Today is {}", today);

        // 1. DB에 이미 있다면 DB에서 가져옴
        BtcPrice btcPrice = btcPriceRepository.findByCoinDate(today);
        if (btcPrice.getKeyword() != null) {
            log.info("Found price in DB: {}", btcPrice.getPrice());
            return btcPrice.getKeyword();
        }

        String kw = openAiService.getKeyword(todayNewsList);
        BtcPrice newBtcPrice = BtcPrice.builder()
                .coinDate(btcPrice.getCoinDate())
                .price(btcPrice.getPrice())
                .keyword(kw)
                .build();
        btcPriceRepository.save(newBtcPrice);
        return kw;
    }
}
