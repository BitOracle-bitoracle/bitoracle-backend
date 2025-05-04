package com.BitOracle.BitOracle.converter;

import com.BitOracle.BitOracle.domain.Prediction;
import com.BitOracle.BitOracle.dto.PredictionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PredictionConverter {

    public static PredictionResponseDto.selectUpDownResponseDto toSelectUpDownResponseDto(Prediction prediction){
        return PredictionResponseDto.selectUpDownResponseDto.builder()
                .predictId(prediction.getPredictId())
                .upDown(prediction.getUpDown())
                .build();
    }
}
