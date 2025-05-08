package com.BitOracle.BitOracle.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class PortfolioResDto {
    private String coinName;
    private BigDecimal quantity;
    private  double averageBuyPrice;
    private BigDecimal totalBuyAmount;
    private double currentPrice;
    private double evaluatuonAmount;
    private double profitRate;
}
