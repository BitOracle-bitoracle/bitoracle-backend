package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.News;
import com.BitOracle.BitOracle.domain.enums.NewsType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiService.class);
    @Value("${spring.openai.secret}")
    private String openaiKey;
    private final @Qualifier("openAiClient") WebClient openAiClient;

    public NewsType classifyNews(String title, String summary) {
        String prompt = String.format("""
                아래 코인 뉴스는 호재인지 악재인지 분류해줘.
                결과는 반드시 '호재' 또는 '악재'로만 줘.
                제목: %s
                요약: %s
                """, title, summary);

        String response = openAiClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + openaiKey)
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", List.of(
                                Map.of("role", "user", "content", prompt)
                        )
                ))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.warn("OpenAI 응답: {}", body))  // 여기에 로그 찍기
                .block();

        return response.contains("호재") ? NewsType.GOOD : NewsType.BAD;
    }

    public String getKeyword(List<News> todayNewsList){
        // 1. 제목만 뽑아서 한 줄로 합치기
        String titles = todayNewsList.stream()
                .map(News::getNewsTitle)
                .reduce((a, b) -> a + "\n" + b)  // 줄바꿈으로 이어붙이기
                .orElse("");

        // 2. 프롬프트 구성
        String prompt = String.format("""
            너는 암호화폐 뉴스 분석 전문가야.
            아래는 오늘의 비트코인 관련 경제 뉴스 제목들이야. 이 뉴스들을 바탕으로 오늘 시장에서 가장 핵심적인 이슈를 표현하는 **키워드 하나만** 뽑아줘.
            
            조건:
            - 반드시 명사형 키워드 하나만 출력해
            - '암호화폐', '경제', '시장' 같은 일반적이고 포괄적인 단어는 금지
            - 뉴스의 공통 주제나 트렌드를 반영해야 해
            - 예: ETF, 반감기, 금리, 규제, 머스크, 기관투자, SEC, 디커플링 등
            
            뉴스 제목:
            %s
            """, titles);

        // 3. GPT 호출
        String response = openAiClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + openaiKey)
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", List.of(
                                Map.of("role", "user", "content", prompt)
                        )
                ))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.warn("OpenAI 키워드 응답: {}", body))
                .block();

        // 4. 응답을 반환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            String keyword = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .trim();
            return keyword;
        } catch (Exception e) {
            e.printStackTrace();
            return "키워드 추출 실패";
        }
    }
}
