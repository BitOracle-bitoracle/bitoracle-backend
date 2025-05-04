package com.BitOracle.BitOracle.dto;


import com.BitOracle.BitOracle.domain.enums.PredictType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PredictionResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class selectUpDownResponseDto{
        @JsonProperty("predict_id")
        private Long predictId;

        @JsonProperty("up_down")
        private PredictType upDown;
    }
}
