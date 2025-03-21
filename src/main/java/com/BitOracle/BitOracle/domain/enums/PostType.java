package com.BitOracle.BitOracle.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PostType {
    NORMAL("일반글"),
    POPULAR("인기글"),
    COLUMN("칼럼");

    private final String type;
}
