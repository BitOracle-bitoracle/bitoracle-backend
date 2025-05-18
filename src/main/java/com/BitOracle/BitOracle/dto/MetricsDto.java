package com.BitOracle.BitOracle.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
public class MetricsDto {
    private BigDecimal marketCap;
    private BigDecimal btcDominance;
    private BigDecimal kimchiPremium;
}
