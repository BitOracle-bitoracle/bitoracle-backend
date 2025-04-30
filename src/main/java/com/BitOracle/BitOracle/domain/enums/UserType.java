package com.BitOracle.BitOracle.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserType {

    USER("일반유저"),
    EXPERT("전문가");

    private final String newType;
}
