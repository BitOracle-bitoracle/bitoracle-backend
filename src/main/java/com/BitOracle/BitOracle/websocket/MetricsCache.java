package com.BitOracle.BitOracle.websocket;

import com.BitOracle.BitOracle.dto.MetricsDto;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class MetricsCache {

    private final AtomicReference<MetricsDto> cache = new AtomicReference<>();

    public void update(MetricsDto metrics) {
        cache.set(metrics);
    }

    public MetricsDto get() {
        return cache.get();
    }
}
