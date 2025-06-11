package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.converter.PredictionConverter;
import com.BitOracle.BitOracle.domain.Prediction;
import com.BitOracle.BitOracle.domain.Record;
import com.BitOracle.BitOracle.dto.PredictionRequestDto;
import com.BitOracle.BitOracle.dto.PredictionResponseDto;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping(value = "predict/select")
    public DataResponseDto<PredictionResponseDto.selectUpDownResponseDto> selectUpDown(
            @RequestHeader("Authorization") String authorization,
            @RequestBody PredictionRequestDto.PredictionUpDownRequestDto predictionUpDownRequestDto){

        Prediction prediction = predictionService.selectUpDown(authorization, predictionUpDownRequestDto);
        PredictionResponseDto.selectUpDownResponseDto responseDto = PredictionConverter.toSelectUpDownResponseDto(prediction);

        return DataResponseDto.of(responseDto, "UP/DOWN 예측 정보를 저장했습니다.");
    }

    @GetMapping(value = "predict/calendar")
    public DataResponseDto<List<PredictionResponseDto.getCalendarResponseDto>> getCalendar(
            @RequestHeader("Authorization") String authorization){

        List<Prediction> predictionList = predictionService.getCalendar(authorization);
        List<PredictionResponseDto.getCalendarResponseDto> responseDto = PredictionConverter.toGetCalendarResponseDto(predictionList);

        return DataResponseDto.of(responseDto, "해당 유저의 달력 히스토리 조회를 위한 모든 예측 기록을 조회했습니다.");
    }

    @GetMapping(value = "predict/stats")
    public DataResponseDto<PredictionResponseDto.getStatsResponseDto> getStats(
            @RequestHeader("Authorization") String authorization){

        Record record = predictionService.getStats(authorization);
        PredictionResponseDto.getStatsResponseDto responseDto = PredictionConverter.toGetStatsResponseDto(record);

        return DataResponseDto.of(responseDto, "해당 유저의 예측 통계 정보를 조회했습니다.");
    }

    @GetMapping(value = "predict/check")
    public DataResponseDto<PredictionResponseDto.CheckPredictionResponseDto> checkTodayPrediction(
            @RequestHeader("Authorization") String authorization) {

        Prediction prediction = predictionService.checkTodayPrediction(authorization);
        PredictionResponseDto.CheckPredictionResponseDto responseDto =
                PredictionConverter.toCheckPredictionResponseDto(prediction);

        return DataResponseDto.of(responseDto, "오늘 예측 여부를 확인했습니다.");
    }
}
