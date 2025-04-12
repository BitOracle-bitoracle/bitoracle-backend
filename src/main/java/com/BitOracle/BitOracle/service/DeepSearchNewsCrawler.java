package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.dto.CrawledNewsDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeepSearchNewsCrawler {

    private final WebClient deepSearchClient;

    @Value("${spring.deepsearch.key}")
    private String deepSearchKey;

    public List<CrawledNewsDto> fetchNews() {
        String keyword = "비트코인 OR 블록체인 OR 암호화폐 OR 가상화폐"; // 키워드 조정 가능

        Mono<String> responseMono = deepSearchClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/global-articles")
                        .queryParam("keyword", keyword)
                        .queryParam("page_size", 20)
                        .queryParam("api_key", deepSearchKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);

        String response = responseMono.block(); // 비동기 처리 필요 시 조정 가능
        log.info("딥서치 응답: {}", response);

        return parse(response);
    }

    public List<CrawledNewsDto> parse(String json) {
        List<CrawledNewsDto> result = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode data = root.get("data");

            for (JsonNode node : data) {
                String title = node.get("title_ko").asText();
                String summary = node.get("summary_ko").asText();
                String url = node.get("content_url").asText();
                String imageUrl = node.get("image_url").isNull() ? null : node.get("image_url").asText();

                CrawledNewsDto dto = CrawledNewsDto.builder()
                        .title(title)
                        .summary(summary)
                        .url(url)
                        .imageUrl(imageUrl)
                        .build();

                result.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}

