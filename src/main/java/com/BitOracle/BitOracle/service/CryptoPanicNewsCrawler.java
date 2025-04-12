package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.dto.CrawledNewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
// CryptoPanic은 자체 사이트에서 뉴스 api를 제공해주지만 영어 뉴스이고, 이미지가 없기 때문에 우리 서비스와 적합하지 않아서 현재는 사용하지 않음
public class CryptoPanicNewsCrawler {

    @Value("${spring.cryptopanic.key}")
    private String apiKey;

    private final @Qualifier("cryptoPanicClient") WebClient cryptoPanicClient;

    public List<CrawledNewsDto> fetchNews() {
        String response = cryptoPanicClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/posts/")
                        .queryParam("auth_token", apiKey)
                        .queryParam("currencies", "BTC")
                        .queryParam("public", "true")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<CrawledNewsDto> result = new ArrayList<>();

        try {
            var jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response);
            var articles = jsonNode.get("results");

            for (var article : articles) {
                String title = article.get("title").asText();
                String url = article.get("url").asText();
                String summary = article.has("content") && !article.get("content").isNull() ? article.get("content").asText() : "";
                String imageUrl = article.has("thumbnail") && !article.get("thumbnail").isNull() ? article.get("thumbnail").asText() : null;

                result.add(CrawledNewsDto.builder()
                        .title(title)
                        .url(url)
                        .summary(summary)
                        .imageUrl(imageUrl)
                        .build());
            }
        } catch (Exception e) {
            log.error("CryptoPanic 응답 파싱 실패", e);
        }

        return result;
    }
}
