package com.BitOracle.BitOracle.converter;

import com.BitOracle.BitOracle.domain.Prediction;
import com.BitOracle.BitOracle.domain.Record;
import com.BitOracle.BitOracle.dto.NewsResponseDto;
import com.BitOracle.BitOracle.dto.PredictionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PredictionConverter {

    public static PredictionResponseDto.selectUpDownResponseDto toSelectUpDownResponseDto(Prediction prediction){
        return PredictionResponseDto.selectUpDownResponseDto.builder()
                .predictId(prediction.getPredictId())
                .upDown(prediction.getUpDown())
                .build();
    }

    public static List<PredictionResponseDto.getCalendarResponseDto> toGetCalendarResponseDto(List<Prediction> predictionList){
        return predictionList.stream()
                .map(prediction -> PredictionResponseDto.getCalendarResponseDto.builder()
                        .predictId(prediction.getPredictId())
                        .createdAt(LocalDate.from(prediction.getCreatedAt()))
                        .upDown(prediction.getUpDown())
                        .correct(prediction.getIsCorrect())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public static PredictionResponseDto.getStatsResponseDto toGetStatsResponseDto(Record record){
        return PredictionResponseDto.getStatsResponseDto.builder()
                .recordId(record.getRecordId())
                .trial(record.getTrial())
                .success(record.getSuccess())
                .failure(record.getFailure())
                .build();
    }

    public static PredictionResponseDto.CheckPredictionResponseDto toCheckPredictionResponseDto(Prediction prediction) {
        if (prediction == null) {
            return PredictionResponseDto.CheckPredictionResponseDto.builder()
                    .predicted(false)
                    .upDown(null)
                    .build();
        }

        return PredictionResponseDto.CheckPredictionResponseDto.builder()
                .predicted(true)
                .upDown(prediction.getUpDown())
                .build();
    }
}
