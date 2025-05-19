package com.BitOracle.BitOracle.websocket;

import com.BitOracle.BitOracle.service.RealTimePriceService;
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

    private final RealTimePriceService realTimePriceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String subscribeMessage = "[{\"ticket\":\"multi_coin_chart\"}, " +
                "{\"type\":\"ticker\", \"codes\":[" +
                "\"KRW-BTC\", \"KRW-ETH\"" +
                "]}]";
        session.sendMessage(new TextMessage(subscribeMessage));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // 바이너리 데이터를 문자열로 변환 (UTF-8)
        String payload = new String(message.getPayload().array(), StandardCharsets.UTF_8);

        //배열 형태 json 처리
        JsonNode root = objectMapper.readTree(payload);
        if(root.isArray()) {
            for (JsonNode json : root) {
                sendParsedTradeData(json);
            }
        }
        else{
            sendParsedTradeData(root);
        }
    }

    //코인 1개에 대한 처리 로직
    private void sendParsedTradeData(JsonNode json) {
        String code = json.get("code").asText();             // ex: KRW-BTC
        double price = json.get("trade_price").asDouble();     // 현재가
        String date = json.get("trade_date").asText();
        String time = json.get("trade_timestamp").asText();

        //실시간 시세 저장
        String coinName = code;//KRW-BTC
        realTimePriceService.updatePrice(coinName, price); //업데이트

        Map<String, Object> data = new HashMap<>();
        data.put("code", code);
        data.put("price", price);
        data.put("date", date);
        data.put("time", time);
        //log.info("📡 받은 바이너리 데이터: " + data);
        messagingTemplate.convertAndSend("/sub/trade", data);
    }
}
