package com.BitOracle.BitOracle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class BtcPriceResponseDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BtcPriceMidnightResponseDto {
        BigDecimal price;
    }
}
