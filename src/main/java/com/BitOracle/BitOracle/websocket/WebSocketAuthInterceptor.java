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

        log.info("==== WebSocketAuthInterceptor 실행됨 ====");
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies == null) return false;

            for (Cookie cookie : cookies) {
                if ("access".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ token"+token);
                    if (!jwtUtil.isExpired(token)) { // 수정 필요
                        String username = jwtUtil.getUsername(token);
                        attributes.put("username", username); //username 저장
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
