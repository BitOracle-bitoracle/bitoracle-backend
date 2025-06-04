package com.BitOracle.BitOracle.converter;

import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    public static UserResponseDto.userinfoResponseDto toUserinfoResponseDto(User user){
        return UserResponseDto.userinfoResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .userType(user.getUserType())
                .point(user.getPoint())
                .build();
    }
}
