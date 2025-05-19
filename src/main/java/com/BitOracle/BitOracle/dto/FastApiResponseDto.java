package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.dto.PredictionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FastApiResponseDto {
    private List<PredictionDto> predictions;
    // Getters and Setters
}