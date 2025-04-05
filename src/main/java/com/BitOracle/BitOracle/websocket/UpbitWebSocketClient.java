package com.BitOracle.BitOracle.websocket;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitWebSocketClient {

    private final UpbitWebSocketHandler handler;

    @PostConstruct
    public void connect() {
        WebSocketClient client = new StandardWebSocketClient();
        client.doHandshake(handler, "wss://api.upbit.com/websocket/v1");
        log.info("업비트 api WebSocket 연결됨@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
}
