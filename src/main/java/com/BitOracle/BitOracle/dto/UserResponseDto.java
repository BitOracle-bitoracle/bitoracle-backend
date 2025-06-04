package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.domain.enums.PredictType;
import com.BitOracle.BitOracle.domain.enums.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class userinfoResponseDto{
        @JsonProperty("user_id")
        private Long userId;

        private String nickname;

        @JsonProperty("user_type")
        private UserType userType;

        private Integer point;
    }
}
