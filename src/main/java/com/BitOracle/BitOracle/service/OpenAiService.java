package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.enums.NewsType;
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
}
