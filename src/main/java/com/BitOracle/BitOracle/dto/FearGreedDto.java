package com.BitOracle.BitOracle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FearGreedDto {
    private String value;           // 지수 값 (0~100)
    private String value_classification; // Fear, Greed
    private String timestamp;
}
