package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.RunNewsPipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/test")
@RestController
@RequiredArgsConstructor
public class TestController {

    private final RunNewsPipeline runNewsPipeline;
    private final JWTUtil jwtUtil;

    @GetMapping("/auth/test")
    public String testLogin() {
        return "✅ 로그인된 사용자입니다.";
    }

    @GetMapping("/run-news-pipeline")
    public String runNewsPipelineNow() throws InterruptedException {
        runNewsPipeline.runNewsPipeline();
        return "뉴스 파이프라인 실행 완료!";
    }

    @GetMapping("/token")
    public ResponseEntity<String> generateToken() {
        // 테스트용 username, role 설정
        String username = "testuser";
        String role = "ROLE_USER";

        // 1시간짜리 access token 생성
        String accessToken = jwtUtil.createJwt("access", username, role, 3600000L);

        return ResponseEntity.ok(accessToken);
    }
}
