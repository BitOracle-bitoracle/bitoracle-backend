package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.converter.PredictionConverter;
import com.BitOracle.BitOracle.domain.Prediction;
import com.BitOracle.BitOracle.dto.PredictionRequestDto;
import com.BitOracle.BitOracle.dto.PredictionResponseDto;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping(value = "predict/select")
    public DataResponseDto<PredictionResponseDto.selectUpDownResponseDto> selectUpDown(
            @CookieValue("access") String authorization,
            @RequestBody PredictionRequestDto.PredictionUpDownRequestDto predictionUpDownRequestDto){

        Prediction prediction = predictionService.selectUpDown(authorization, predictionUpDownRequestDto);
        PredictionResponseDto.selectUpDownResponseDto responseDto = PredictionConverter.toSelectUpDownResponseDto(prediction);

        return DataResponseDto.of(responseDto, "UP/DOWN 예측 정보를 저장했습니다.");
    }
}
