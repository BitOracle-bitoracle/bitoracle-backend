package com.BitOracle.BitOracle.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitWebSocketHandler extends BinaryWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String subscribeMessage = "[{\"ticket\":\"btc_chart\"}, {\"type\":\"ticker\", \"codes\":[\"KRW-BTC\"]}]";
        session.sendMessage(new TextMessage(subscribeMessage));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // 바이너리 데이터를 문자열로 변환 (UTF-8)
        String payload = new String(message.getPayload().array(), StandardCharsets.UTF_8);
        // JSON 파싱
        JsonNode json = objectMapper.readTree(payload);
        double price = json.get("trade_price").asDouble(); // 현재가
        String date = json.get("trade_date").asText();     // ex: 20250405
        String time = json.get("trade_time").asText();     // ex: 231023

        // 프론트로 전송할 데이터 구성
        Map<String, Object> data = new HashMap<>();
        data.put("price", price);
        data.put("date", date);
        data.put("time", time);
        log.info("📡 받은 바이너리 데이터: " + data);
        // 프론트에 broadcast (STOMP 채널)
        messagingTemplate.convertAndSend("/sub/trade", data);
    }
}
