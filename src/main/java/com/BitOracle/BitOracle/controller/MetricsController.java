package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.dto.FearGreedDto;
import com.BitOracle.BitOracle.dto.MetricsDto;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.MetricsService;
import com.BitOracle.BitOracle.websocket.MetricsCache;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MetricsController {

    private final MetricsCache metricsCache;
    private final MetricsService metricsService;

    @GetMapping("/metrics")
    public MetricsDto getCachedMetrics() {
        return metricsCache.get();
    }

    @GetMapping("/metrics/fear-greed")
    public DataResponseDto<FearGreedDto> getFearGreedIndex() {
        FearGreedDto dto = metricsService.fetchFearGreedIndex();
        return DataResponseDto.of(dto, "공포와 탐욕 지수를 조회했습니다.");
    }
}

