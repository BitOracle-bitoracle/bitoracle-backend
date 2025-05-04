package com.BitOracle.BitOracle.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.BitOracle.BitOracle.dto.CustomOAuth2User;
import com.BitOracle.BitOracle.dto.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

public class JWTFilter extends OncePerRequestFilter { // 한 번만 요청이 되면 됨

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access키에 담긴 토큰을 꺼냄
//        String accessToken = request.getHeader("access");

        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("access")) {
                    accessToken = cookie.getValue();
                }
            }
        }
        System.out.println("access token : " + accessToken);

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        if (jwtUtil.isExpired(accessToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) { // access 토큰이 아닌 경우

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 다음 필터로 넘기지 않고 상태 코드를 response함, 이때 코드는 프론트와 협의
            return;
        }
        // 토큰 검증 완료

        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);
        System.out.println("username : " + username);
        String role = jwtUtil.getRole(accessToken);

        UserDTO userEntity = new UserDTO();
        userEntity.setUsername(username);
        userEntity.setRole(role);
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
        // access, refresh 토큰 두 개를 발급해 이용하기 위해 주석처리
//        String requestUri = request.getRequestURI();
//
//        if (requestUri.matches("^\\/login(?:\\/.*)?$")) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//        if (requestUri.matches("^\\/oauth2(?:\\/.*)?$")) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        //cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
//        String authorization = null;
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies){
//
//            if (cookie.getName().equals("Authorization")) { // key로 "Authorization"을 가진 쿠키를 찾아서
//
//                authorization = cookie.getValue(); // authorization 문자열에 값을 담음
//            }
//        }
//
//        //Authorization 헤더 검증
//        // authorization이 null 이면 다음 필터로 넘김
//        if (authorization == null) {
//
//            System.out.println("token null");
//            filterChain.doFilter(request, response);
//
//            //조건이 해당되면 메소드 종료 (필수)
//            return;
//        }
//
//        String token = authorization;
//
//        //토큰 소멸 시간 검증
//        // 토큰이 소멸되었으면 다음 필터로 넘김
//        if (jwtUtil.isExpired(token)) {
//
//            System.out.println("token expired");
//            filterChain.doFilter(request, response);
//
//            //조건이 해당되면 메소드 종료 (필수)
//            return;
//        }
//
//        //토큰에서 username과 role 획득
//        String username = jwtUtil.getUsername(token);
//        String role = jwtUtil.getRole(token);
//
//        //userDTO를 생성하여 값 set => 내 코드에서 다루기 편하게 userDTO에 넣어줌
//        UserDTO userDTO = new UserDTO();
//        userDTO.setUsername(username);
//        userDTO.setRole(role);
//
//        //UserDetails에 회원 정보 객체 담기
//        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
//
//        //스프링 시큐리티 인증 토큰 생성
//        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
//        //세션에 사용자 등록
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        filterChain.doFilter(request, response);
    }
}
