package com.BitOracle.BitOracle.dto;

import lombok.Data;

import java.util.List;

@Data
public class BitcoinHistoryResponseDto {
    private List<PriceHistoryRequestDto> prices;
}