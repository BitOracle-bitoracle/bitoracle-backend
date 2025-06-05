package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.dto.CoinMarketCapResponse;
import com.BitOracle.BitOracle.dto.FearGreedDto;
import com.BitOracle.BitOracle.dto.MetricsDto;
import com.BitOracle.BitOracle.dto.UpbitTickerResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class MetricsService {

    public MetricsDto fetchMetrics() {
        MetricsDto marketMetrics = fetchMarketMetrics();
        BigDecimal kimchiPremium = calculateKimchiPremium();

        return MetricsDto.builder()
                .marketCap(marketMetrics.getMarketCap())
                .btcDominance(marketMetrics.getBtcDominance())
                .kimchiPremium(kimchiPremium)
                .build();
    }

    public MetricsDto fetchMarketMetrics() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = "https://api.coingecko.com/api/v3/global";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.get("data");

            BigDecimal marketCap = new BigDecimal(dataNode.get("total_market_cap").get("usd").asText());
            BigDecimal btcDominance = new BigDecimal(dataNode.get("market_cap_percentage").get("btc").asText());

            return MetricsDto.builder()
                    .marketCap(marketCap)
                    .btcDominance(btcDominance)
                    .build();

        } catch (Exception e) {
            log.error("📉 CoinGecko 시가총액/도미넌스 조회 실패", e);
            return null;
        }
    }

    public BigDecimal calculateKimchiPremium() {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 1. 업비트 KRW-BTC 가격
            String upbitUrl = "https://api.upbit.com/v1/ticker?markets=KRW-BTC";
            ResponseEntity<UpbitTickerResponse[]> upbitResp =
                    restTemplate.getForEntity(upbitUrl, UpbitTickerResponse[].class);

            BigDecimal upbitPrice = new BigDecimal(upbitResp.getBody()[0].getTrade_price());

            // 2. CoinGecko BTC/USD 가격
            String coingeckoUrl = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd";
            ResponseEntity<JsonNode> cgResp =
                    restTemplate.getForEntity(coingeckoUrl, JsonNode.class);
            BigDecimal globalPrice = new BigDecimal(cgResp.getBody().get("bitcoin").get("usd").asText());

            // 3. 환율
            BigDecimal exchangeRate = getUsdToKrwRate();
//            BigDecimal exchangeRate = BigDecimal.valueOf(1350); // 임시 하드 코딩

            // 4. 글로벌 BTC 가격 (KRW 환산)
            BigDecimal globalPriceKrw = globalPrice.multiply(exchangeRate);

            // 5. 김치 프리미엄 계산
            BigDecimal kimchiPremium = upbitPrice.subtract(globalPriceKrw)
                    .divide(globalPriceKrw, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

//            log.info("🟢 업비트 BTC(KRW): {}", upbitPrice);
//            log.info("🌐 글로벌 BTC(USD): {}", globalPrice);
//            log.info("💱 환율(USD→KRW): {}", exchangeRate);
//            log.info("🌐 글로벌 BTC(KRW): {}", globalPriceKrw);
//            log.info("📊 김치 프리미엄: {}%", kimchiPremium);

            return kimchiPremium;

        } catch (Exception e) {
            log.error("김치 프리미엄 계산 실패", e);
            return BigDecimal.ZERO;
        }
    }

    public FearGreedDto fetchFearGreedIndex() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = "https://api.alternative.me/fng/";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.get("data").get(0); // 최신 데이터 1개

            return FearGreedDto.builder()
                    .value(dataNode.get("value").asText())
                    .value_classification(dataNode.get("value_classification").asText())
                    .timestamp(dataNode.get("timestamp").asText())
                    .build();

        } catch (Exception e) {
            log.error("공포·탐욕 지수 조회 실패", e);
            return null;
        }
    }

    public BigDecimal getUsdToKrwRate() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.exchangerate.fun/latest?base=USD";

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            BigDecimal rate = new BigDecimal(root.get("rates").get("KRW").asText());

            return rate.setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
}
