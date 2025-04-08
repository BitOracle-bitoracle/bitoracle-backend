package com.BitOracle.BitOracle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BtcPriceResponseDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BtcPriceMidnightResponseDto {
        LocalDate today;
        BigDecimal price;
    }
}
