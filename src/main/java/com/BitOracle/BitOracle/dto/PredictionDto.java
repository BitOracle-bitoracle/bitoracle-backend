package com.BitOracle.BitOracle.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PredictionDto {
    private String date;
    private double predicted;
    private double actual;
    /*    private List<PredictedValue> pastPredictions;
    private List<PredictedValue> futurePredictions;*/

    // getters and setters
}

