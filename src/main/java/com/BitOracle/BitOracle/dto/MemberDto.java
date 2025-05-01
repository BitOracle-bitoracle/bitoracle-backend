package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.enums.UserType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {

    private UserType userType;
    private String nickName;
    private Integer point;



    @Builder
    public MemberDto(User user) {
        this.nickName = user.getNickname();
        this.userType = user.getUserType();
        this.point = user.getPoint();
    }
}
