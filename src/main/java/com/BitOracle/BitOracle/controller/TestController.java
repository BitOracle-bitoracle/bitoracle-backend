package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.service.RunNewsPipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/test")
@RestController
@RequiredArgsConstructor
public class TestController {

    private final RunNewsPipeline runNewsPipeline;

    @GetMapping("/auth/test")
    public String testLogin() {
        return "✅ 로그인된 사용자입니다.";
    }

    @GetMapping("/run-news-pipeline")
    public String runNewsPipelineNow() throws InterruptedException {
        runNewsPipeline.runNewsPipeline();
        return "뉴스 파이프라인 실행 완료!";
    }
}
