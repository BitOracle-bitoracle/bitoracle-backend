package com.BitOracle.BitOracle.dto;


import com.BitOracle.BitOracle.domain.enums.PredictType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getCalendarResponseDto{
        @JsonProperty("predict_id")
        private Long predictId;

        @JsonProperty("created_at")
        private LocalDate createdAt;

        @JsonProperty("up_down")
        private PredictType upDown;

        @JsonProperty("correct")
        private String correct;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getStatsResponseDto{
        @JsonProperty("record_id")
        private Long recordId;

        private int trial;

        private int success;

        private int failure;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckPredictionResponseDto {
        @JsonProperty("predicted")
        private boolean predicted;

        @JsonProperty("upDown")
        private PredictType upDown; // 없으면 null
    }
}
