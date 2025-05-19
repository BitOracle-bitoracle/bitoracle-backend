package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.PredictedHistory;
import com.BitOracle.BitOracle.domain.Prediction;
import com.BitOracle.BitOracle.domain.PriceHistory;
import com.BitOracle.BitOracle.dto.BitcoinHistoryResponseDto;
import com.BitOracle.BitOracle.dto.FastApiResponseDto;
import com.BitOracle.BitOracle.dto.PredictionDto;
import com.BitOracle.BitOracle.dto.PriceHistoryRequestDto;
import com.BitOracle.BitOracle.repository.PredictedHistoryRepository;
import com.BitOracle.BitOracle.repository.PriceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class BtcPredictService {
    @Autowired
    private  PriceHistoryRepository priceHistoryRepository;
    @Autowired
    private PredictedHistoryRepository predictedHistoryRepository;
    private final AtomicReference<PredictionDto> cachedPrediction = new AtomicReference<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private List<PredictionDto> cachedPredictions;



    // 매일 00:00에 실행 (자정)
    @Scheduled(cron = "0 0 0 * * *")
    public List<PredictionDto> fetchPrediction() {
        String url = "http://localhost:8000/predict";  // 로컬 테스트용 FastAPI 주소

        LocalDate endDate = LocalDate.now();
        // 6개월 전 날짜 계산
        LocalDate startDate = LocalDate.now().minusMonths(6);

        // POST 요청용 데이터 준비
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("start_date", startDate.toString());
        requestBody.put("end_date", endDate.toString());
        requestBody.put("window_size", 90);
        requestBody.put("future_prediction_days", 10);

        // POST 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // 요청 보내기
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<FastApiResponseDto> response = restTemplate.postForEntity(url, requestEntity, FastApiResponseDto.class);
            FastApiResponseDto predictionResponse = response.getBody();

            if (predictionResponse != null) {
                List<PredictionDto> predictions = predictionResponse.getPredictions();

                for (PredictionDto dto : predictions) {
                    LocalDate date = LocalDate.parse(dto.getDate());

                    // 1. PredictedHistory 처리
                    predictedHistoryRepository.findByDate(date).orElseGet(() -> {
                        PredictedHistory newPredicted = PredictedHistory.builder()
                                .date(date)
                                .predicted(dto.getPredicted())
                                .createdAt(LocalDate.now())
                                .build();
                        return predictedHistoryRepository.save(newPredicted);
                    });
                    // 2. PriceHistory 처리 (actual 값)
/*                    priceHistoryRepository.findByDate(date).orElseGet(() -> {
                        if (dto.getActual() != 0) {
                            PriceHistory newPrice = PriceHistory.builder()
                                    .date(date)
                                    .actual(dto.getActual())
                                    .createdAt(LocalDate.now())
                                    .build();
                            return priceHistoryRepository.save(newPrice);
                        }
                        return null;
                    });*/
                }
                return predictions;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Failed to fetch prediction: " + e.getMessage());
            return null;
        }
    }

    public PredictionDto getCachedPrediction() {
        return cachedPrediction.get();
    }


    public void fetchAndSavePriceHistory(String startDate, String endDate) {
        String url = "http://localhost:8000/bitcoin/history?start_date=" + startDate + "&end_date=" + endDate;

        ResponseEntity<PriceHistoryRequestDto[]> response = restTemplate.getForEntity(url, PriceHistoryRequestDto[].class);
        PriceHistoryRequestDto[] priceHistories = response.getBody();

        if (priceHistories != null) {
            for (PriceHistoryRequestDto dto : priceHistories) {
                LocalDate date = LocalDate.parse(dto.getDate());

                // ✅ 중복 저장 방지
                if (!priceHistoryRepository.existsByDate(date)) {
                    PriceHistory entity = PriceHistory.builder()
                            .date(date)
                            .actual(dto.getActual())
                            .createdAt(LocalDate.parse(dto.getCreatedAt()))
                            .build();
                    priceHistoryRepository.save(entity);
                }
            }
        }
    }
}
