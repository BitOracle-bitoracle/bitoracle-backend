package com.BitOracle.BitOracle.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NewsType {
    GOOD("호재"),
    BAD("악재");

    private final String newType;
}
