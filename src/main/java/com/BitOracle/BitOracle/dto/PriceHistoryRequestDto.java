package com.BitOracle.BitOracle.dto;

import lombok.Data;

@Data
public class PriceHistoryRequestDto {
    private String date;
    private Double actual;
    private String createdAt;
}