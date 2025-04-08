package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.BtcPrice;
import com.BitOracle.BitOracle.repository.BtcPriceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
@Slf4j
public class BtcPriceService {
    private final BtcPriceRepository btcPriceRepository;

    public BtcPrice getMidnightBtc(){
        LocalDate today = LocalDate.now();
        log.info("Today is {}", today);

        // 1. DB에 이미 있다면 DB에서 가져옴
        BtcPrice btcPrice = btcPriceRepository.findByCoinDate(today);
        if (btcPrice != null) {
            log.info("Found price in DB: {}", btcPrice.getPrice());
            return btcPrice;
        }

        try {
            // 2. 업비트 API 호출 (금일 00시 기준 가격)
            ZonedDateTime todayMidnightKst = today.atStartOfDay(ZoneId.of("Asia/Seoul"));
            String toDate = todayMidnightKst.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")); // RFC3339

            String url = "https://api.upbit.com/v1/candles/days?market=KRW-BTC&count=1&to=" + toDate;
            log.info("요청할 URL: {}", url);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode candle = root.get(0);

            BigDecimal openingPrice = candle.get("opening_price").decimalValue();

            // 3. DB에 저장
            BtcPrice newPrice = BtcPrice.builder()
                    .coinDate(today)
                    .price(openingPrice)
                    .build();

            btcPriceRepository.save(newPrice);
            log.info("Saved new BTC Date: {}, price: {}", today, openingPrice);

            return newPrice;
        } catch (Exception e) {
            log.error("Error while calling Upbit API", e);
            throw new RuntimeException("업비트 API 호출 실패", e);
        }
    }
}
