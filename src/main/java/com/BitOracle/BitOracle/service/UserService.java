package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.repository.UserEntityRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final JWTUtil jwtUtil;

    public User getUser(String authorization){
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        return userEntity.getUser();
    }
}
