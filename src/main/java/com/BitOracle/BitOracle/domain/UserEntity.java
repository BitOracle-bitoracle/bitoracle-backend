package com.BitOracle.BitOracle.domain;

import com.BitOracle.BitOracle.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String name;
    private String email;
    private String role;

    // UserEntity는 로그인 정보만 관리, User는 다른 테이블들과 관계를 가지도록 구분하기 위해
    // UserEntity가 User를 참조하도록 설계
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
}
