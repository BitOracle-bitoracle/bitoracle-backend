package com.BitOracle.BitOracle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeRes {
    private boolean islike;
    private long likeCount;
}
