package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.domain.PredictedHistory;
import com.BitOracle.BitOracle.domain.PriceHistory;
import com.BitOracle.BitOracle.dto.PredictChartDto;
import com.BitOracle.BitOracle.dto.PredictionDto;
import com.BitOracle.BitOracle.dto.PriceChartDto;
import com.BitOracle.BitOracle.dto.PriceHistoryRequestDto;
import com.BitOracle.BitOracle.repository.PredictedHistoryRepository;
import com.BitOracle.BitOracle.repository.PriceHistoryRepository;
import com.BitOracle.BitOracle.service.BtcPredictService;
import com.BitOracle.BitOracle.service.BtcPriceService;
import com.BitOracle.BitOracle.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class BtcPredictController {

    private final BtcPredictService btcPredictService;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PredictedHistoryRepository  predictedHistoryRepository;

    @GetMapping("/predict-now")
    public List<PredictionDto> predictNow() {
        return btcPredictService.fetchPrediction();
    }

    @GetMapping("/prediction")
    public PredictionDto getCachedPrediction() {
        return btcPredictService.getCachedPrediction();
    }

    @PostMapping("/real-price")
    public String fetchPriceHistory(@RequestParam String startDate,
                                    @RequestParam String endDate) {
        btcPredictService.fetchAndSavePriceHistory(startDate, endDate);
        return "비트코인 가격 데이터를 저장했습니다.";
    }

    @GetMapping("api/price/chart")
    public ResponseEntity<List<PriceChartDto>> getBitcoinChart() {
        List<PriceHistory> histories = priceHistoryRepository.findAllByOrderByDateAsc();
        List<PriceChartDto> chartData = histories.stream()
                .map(h -> new PriceChartDto(h.getDate().toString(), h.getActual()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/api/predict/chart")
    public ResponseEntity<List<PredictChartDto>> getPredictChartData() {
        List<PredictedHistory> historyList = predictedHistoryRepository.findAllByOrderByDateAsc();
        List<PredictChartDto> chartData = historyList.stream()
                .map(h -> new PredictChartDto(h.getDate().toString(), h.getPredicted()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(chartData);
    }
}