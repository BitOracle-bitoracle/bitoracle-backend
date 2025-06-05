package com.BitOracle.BitOracle.websocket;

import com.BitOracle.BitOracle.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

//        log.info("==== WebSocketAuthInterceptor 실행됨 ====");

        // HttpServletRequest로 변환
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // Authorization 헤더에서 Bearer 토큰 추출
            String authHeader = httpRequest.getParameter("token");
            // log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ token: " + authHeader);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // "Bearer " 이후 토큰만 추출
                log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ token: " + token);

                // 토큰 검증 및 사용자 이름 추출
                if (!jwtUtil.isExpired(token)) {
                    String username = jwtUtil.getUsername(token);
                    attributes.put("username", username); // WebSocket 세션에 username 저장
                    return true;
                }
            }
        }

        return false; // 토큰 없거나 유효하지 않으면 handshake 거부
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
