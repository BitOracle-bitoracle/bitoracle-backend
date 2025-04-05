package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.dto.*;
import com.BitOracle.BitOracle.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

// 리소스 서버로부터 받아 온 유저 정보를 처리하는 서비스
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException { // OAuth2UserRequest 객체가 리소스 서버에서 받는 유저 정보 객체

        OAuth2User oAuth2User = super.loadUser(userRequest); // DefaultOAuth2UserService에서 가져옴
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")){ // 네이버 소셜 로그인으로 온거면 여기서 처리
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")){ // 구글 소셜 로그인으로 온거면 여기서 처리
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {
            return null;
        }

        // 로그인 완료 시 로직은 추후 작성

        // 네이버와 구글에서 온 유저 이름을 우리는 구분해서 관리해줘야 하기 때문에
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        UserEntity existData = userRepository.findByUsername(username);

        if (existData == null){
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setName(oAuth2Response.getName());
            userEntity.setRole("ROLE_USER");

            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2User.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        }
        else {
            // existData.setUsername(username); // 바뀌지 않았을 것이라 업데이트 안해줘도 됨
            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());

            userRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(existData.getUsername());
            userDTO.setName(oAuth2Response.getName()); // 새로 업데이트된 값으로 바꿔줘야함
            userDTO.setRole(existData.getRole());

            return new CustomOAuth2User(userDTO);
        }

//        UserDTO userDTO = new UserDTO();
//        userDTO.setUsername(username);
//        userDTO.setName(oAuth2User.getName());
//        userDTO.setRole("ROLE_USER");
//        return new CustomOAuth2User(userDTO);
    }
}
