package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.converter.UserConverter;
import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.dto.UserResponseDto;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/mypage/userinfo")
    DataResponseDto<UserResponseDto.userinfoResponseDto> getUserinfo(
            @RequestHeader("Authorization") String authorization){

        User user = userService.getUser(authorization);
        UserResponseDto.userinfoResponseDto responseDto = UserConverter.toUserinfoResponseDto(user);

        return DataResponseDto.of(responseDto, "유저 정보 조회했습니다.");
    }
}
