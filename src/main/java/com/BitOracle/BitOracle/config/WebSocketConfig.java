package com.BitOracle.BitOracle.config;

import com.BitOracle.BitOracle.websocket.CustomHandshakeHandler;
import com.BitOracle.BitOracle.websocket.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//sub/trade
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub","/queue"); // 클라이언트 구독 주소 prefix (브로커가 처리함)
        registry.setApplicationDestinationPrefixes("/app"); // 필요x/app 으로 시작하는 stomp 메세지의 경로는 @controller @MessageMaping 메서드로 라우팅 클라이언트 -> 서버 메세지
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /*
        * 위 설정은 WebSocket을 열 수 있는 주소 /ws-upbit을 정의
            클라이언트는 이 주소로 handshake 요청을 보냄
            handshake가 성공하면 WebSocket 연결이 열림
        * */
        //upbit 실시간 시세
        registry.addEndpoint("/ws-upbit")
                .setAllowedOriginPatterns("*");

        // 포트폴리오 실시간 업데이트용
        registry.addEndpoint("/ws-portfolio")
                .addInterceptors(webSocketAuthInterceptor)//인증
                .setHandshakeHandler(new CustomHandshakeHandler()) // 커스텀 핸들러 설정
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}