package com.BitOracle.BitOracle.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@Builder
public class BuyHistoryCreateRequest {
    private String coinName;
    private BigDecimal quantity;
    private Double price; // 수동입력 (null이면 현재가로 대체)
}
