package com.BitOracle.BitOracle.websocket;

import com.BitOracle.BitOracle.dto.MetricsDto;
import com.BitOracle.BitOracle.service.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final MetricsService metricsService;
    private final MetricsCache metricsCache;

//    @Scheduled(fixedRate = 10000) // 10초마다 전송
//    public void publishMetrics() {
//        MetricsDto metrics = metricsService.fetchMetrics();
//        if (metrics != null) {
//            metricsCache.update(metrics);
//            messagingTemplate.convertAndSend("/sub/metrics", metrics);
//            log.info("✅ 지표 정보 전송: {}", metrics);
//        }
//    }
}
