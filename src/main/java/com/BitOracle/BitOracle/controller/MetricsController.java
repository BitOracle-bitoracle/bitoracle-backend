package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.dto.MetricsDto;
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

    @GetMapping("/metrics")
    public MetricsDto getCachedMetrics() {
        return metricsCache.get();
    }
}

