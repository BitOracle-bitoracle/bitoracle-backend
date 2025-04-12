package com.BitOracle.BitOracle.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

//    @Bean
//    public WebClient webClient() {
//        return WebClient.builder()
//                .baseUrl("https://kr.investing.com")  // 기본 도메인
//                .defaultHeader("User-Agent", "Mozilla/5.0 (compatible)")
//                .build();
//    }

    @Bean
    @Qualifier("openAiClient")
    public WebClient openAiClient() {
        return WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    @Qualifier("cryptoPanicClient")
    public WebClient cryptoPanicClient() {
        return WebClient.builder()
                .baseUrl("https://cryptopanic.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    @Qualifier("deepSearchClient")
    public WebClient deepSearchClient() {
        return WebClient.builder()
                .baseUrl("https://api-v2.deepsearch.com")
                .build();
    }
}
