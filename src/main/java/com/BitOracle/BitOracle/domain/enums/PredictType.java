package com.BitOracle.BitOracle.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PredictType {
    GOOD("Up"),
    BAD("DOWN");

    private final String predictType;
}
