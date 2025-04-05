package com.BitOracle.BitOracle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // 개발 환경: 프론트 서버 주소 허용
                .allowedOrigins(
                        "http://localhost:3000",         // 로컬 프론트
                        "https://bitoracle.netlify.app"       // 배포된 프론트 주소
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("Authorization", "Set-Cookie");
    }
}
