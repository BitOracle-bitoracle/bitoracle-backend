package com.BitOracle.BitOracle.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
@ToString
@Builder
@Getter
public class PortfolioResDto {
    private String coinName;
    private BigDecimal quantity;
    private  double averageBuyPrice;
    private double totalBuyAmount;
    private double currentPrice;
    private double evaluatuonAmount;
    private double profitRate;
}
