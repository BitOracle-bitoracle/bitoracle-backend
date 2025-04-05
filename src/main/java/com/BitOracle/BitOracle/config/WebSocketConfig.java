package com.BitOracle.BitOracle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // 클라이언트 구독 주소 prefix (브로커가 처리함)
        registry.setApplicationDestinationPrefixes("/app"); // 필요x/app 으로 시작하는 stomp 메세지의 경로는 @controller @MessageMaping 메서드로 라우팅 클라이언트 -> 서버 메세지
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /*
        * 위 설정은 WebSocket을 열 수 있는 주소 /ws-upbit을 정의
            클라이언트는 이 주소로 handshake 요청을 보냄
            handshake가 성공하면 WebSocket 연결이 열림
        * */
        registry.addEndpoint("/ws-upbit")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
