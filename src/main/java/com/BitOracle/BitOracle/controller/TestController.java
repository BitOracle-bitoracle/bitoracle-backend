package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.BtcPriceService;
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
    private final BtcPriceService btcPriceService;

    @GetMapping("/auth/test")
    public String testLogin() {
        return "✅ 로그인된 사용자입니다.";
    }

    @GetMapping("/run-news-pipeline")
    public String runNewsPipelineNow() throws InterruptedException {
        runNewsPipeline.runNewsPipeline();
        return "뉴스 파이프라인 실행 완료!";
    }

    @GetMapping("/run-save-midnightPrice")
    public String runSaveMidnightPriceNow() throws InterruptedException {
        btcPriceService.saveMidnightBtcPrice();
        return "00시 기준가 저장 완료!";
    }

    @GetMapping("/run-updateIsCorrect")
    public String runUpdateIsCorrectNow() throws InterruptedException {
        btcPriceService.updateIsCorrect();
        return "Prediction의 isCorrect 속성 업데이트 완료!";
    }

    @GetMapping("/run-updateUserRecords")
    public String runUpdateUserRecords() throws InterruptedException {
        btcPriceService.updateUserRecords();
        return "유저 통계 테이블 업데이트 완료!";
    }

}
