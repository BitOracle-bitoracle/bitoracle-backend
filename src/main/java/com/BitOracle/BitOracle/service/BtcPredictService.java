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


    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledFetchPrediction() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);
        fetchPrediction(startDate, endDate);
    }

    public List<PredictionDto> fetchPrediction(LocalDate startDate, LocalDate endDate) {
        String url = "http://52.78.231.143/predict";

        // POST 요청용 데이터 준비
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("start_date", startDate.toString());
        requestBody.put("end_date", endDate.toString());
        requestBody.put("window_size", 90);
        requestBody.put("future_prediction_days", 10);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<FastApiResponseDto> response = restTemplate.postForEntity(url, requestEntity, FastApiResponseDto.class);
            FastApiResponseDto predictionResponse = response.getBody();

            if (predictionResponse != null) {
                List<PredictionDto> predictions = predictionResponse.getPredictions();

                for (PredictionDto dto : predictions) {
                    LocalDate date = LocalDate.parse(dto.getDate());

                    predictedHistoryRepository.findByDate(date).orElseGet(() -> {
                        PredictedHistory newPredicted = PredictedHistory.builder()
                                .date(date)
                                .predicted(dto.getPredicted())
                                .createdAt(LocalDate.now())
                                .build();
                        return predictedHistoryRepository.save(newPredicted);
                    });

                    priceHistoryRepository.findByDate(date).orElseGet(() -> {
                        if (dto.getActual() != 0) {
                            PriceHistory newPrice = PriceHistory.builder()
                                    .date(date)
                                    .actual(dto.getActual())
                                    .createdAt(LocalDate.now())
                                    .build();
                            return priceHistoryRepository.save(newPrice);
                        }
                        return null;
                    });
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
        String url = "http://52.78.231.143/bitcoin/history?start_date=" + startDate + "&end_date=" + endDate;

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
