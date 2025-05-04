package com.BitOracle.BitOracle.dto;


import com.BitOracle.BitOracle.domain.enums.PredictType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class PredictionRequestDto {

    @Getter
    @Setter
    public static class PredictionUpDownRequestDto {
        @NotNull
        private PredictType upDown;
    }
}
