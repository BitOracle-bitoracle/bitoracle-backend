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

    private final String API_KEY = "c23fdae7-d913-4577-a125-33f2f0565dd5"; // 🔐 CoinMarketCap 프로 키
    private final String URL = "https://pro-api.coinmarketcap.com/v1/global-metrics/quotes/latest";

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
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CMC_PRO_API_KEY", API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);

        try {
            // JSON 파싱 간단 처리 (정식으로 하려면 ObjectMapper 사용)
            String body = response.getBody();
            String totalMarketCapStr = body.split("\"total_market_cap\":")[1].split(",")[0];
            String btcDominanceStr = body.split("\"btc_dominance\":")[1].split(",")[0];

            BigDecimal marketCap = new BigDecimal(totalMarketCapStr);
            BigDecimal dominance = new BigDecimal(btcDominanceStr);

            return MetricsDto.builder()
                    .marketCap(marketCap)
                    .btcDominance(dominance)
                    .build();

        } catch (Exception e) {
            log.error("시가총액 파싱 실패", e);
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

            // 2. CMC BTC/USDT 가격
            String cmcUrl = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=BTC&convert=USDT";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-CMC_PRO_API_KEY", API_KEY);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<CoinMarketCapResponse> cmcResp =
                    restTemplate.exchange(cmcUrl, HttpMethod.GET, entity, CoinMarketCapResponse.class);

            String priceStr = cmcResp.getBody()
                    .getData()
                    .get("BTC")
                    .getQuote()
                    .get("USDT")
                    .getPrice();

            BigDecimal globalPrice = new BigDecimal(priceStr);

            // 3. 환율
//            BigDecimal exchangeRate = calculateUsdKrwRate();
            BigDecimal exchangeRate = BigDecimal.valueOf(1400); // 임시 하드 코딩

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
}
