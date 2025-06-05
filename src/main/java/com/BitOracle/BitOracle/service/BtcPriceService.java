package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.BtcPrice;
import com.BitOracle.BitOracle.domain.Prediction;
import com.BitOracle.BitOracle.domain.Record;
import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.enums.PredictType;
import com.BitOracle.BitOracle.repository.BtcPriceRepository;
import com.BitOracle.BitOracle.repository.PredictionRepository;
import com.BitOracle.BitOracle.repository.RecordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class BtcPriceService {
    private final BtcPriceRepository btcPriceRepository;
    private final PredictionRepository predictionRepository;
    private final RecordRepository recordRepository;

    public BtcPrice getMidnightBtc(){
        LocalDate today = LocalDate.now();
        log.info("Today is {}", today);

        // 스케줄링되어 db에 저장된 00시 기준 가격을 가져옴
        BtcPrice btcPrice = btcPriceRepository.findByCoinDate(today);
        log.info("Found price in DB: {}", btcPrice.getPrice());
        return btcPrice;
    }

    @Scheduled(cron = "1 0 9 * * *", zone = "Asia/Seoul") // 00시 0분 1초에 스케줄링 (인스턴스 시차 때문에 이렇게)
    public void saveMidnightBtcPrice() {
        LocalDate today = LocalDate.now();

        try {
            // 1. 오늘 00시 기준 업비트 BTC 가격 저장
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

            // 2. 어제/오늘 가격 비교해서 Prediction isCorrect 업데이트
            updateIsCorrect();
            log.info("Prediction 정답 업데이트 완료");

            // 3. 모든 User Record 갱신
            updateUserRecords();
            log.info("User Record 재계산 및 업데이트 완료");

        } catch (Exception e) {
            log.error("Error while calling Upbit API", e);
            throw new RuntimeException("업비트 API 호출 실패", e);
        }
    }

    public void updateIsCorrect() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 오늘 가격과 어제 가격 가져오기
        BtcPrice todayPriceEntity = btcPriceRepository.findByCoinDate(today);
        BtcPrice yesterdayPriceEntity = btcPriceRepository.findByCoinDate(yesterday);

        if (todayPriceEntity == null || yesterdayPriceEntity == null) {
            log.error("오늘 또는 어제 가격 데이터가 없습니다.");
            throw new RuntimeException("가격 데이터가 부족합니다.");
        }

        BigDecimal todayPrice = todayPriceEntity.getPrice();
        BigDecimal yesterdayPrice = yesterdayPriceEntity.getPrice();

        // 가격 비교해서 방향 결정
        PredictType realUpDown = todayPrice.compareTo(yesterdayPrice) > 0 ? PredictType.UP : PredictType.DOWN;

        log.info("어제 가격: {}, 오늘 가격: {}, 실제 방향: {}", yesterdayPrice, todayPrice, realUpDown);

        // 오늘 날짜의 Prediction 가져와서 isCorrect 업데이트
        List<Prediction> predictions = predictionRepository.findByCreatedAtBetween(today.atStartOfDay(), today.atTime(23, 59, 59));

        for (Prediction prediction : predictions) {
            if (prediction.getUpDown() == realUpDown) {
                prediction.setIsCorrect(true);
            } else {
                prediction.setIsCorrect(false);
            }
        }

        predictionRepository.saveAll(predictions);
        log.info("모든 prediction isCorrect 업데이트 완료");
    }

    public void updateUserRecords() {
        // 모든 유저들의 Record를 업데이트

        // 전체 Prediction 가져오기
        List<Prediction> allPredictions = predictionRepository.findAll();

        // User별로 Prediction을 그룹핑
        Map<User, List<Prediction>> predictionsByUser = allPredictions.stream()
                .collect(Collectors.groupingBy(Prediction::getUser));

        for (Map.Entry<User, List<Prediction>> entry : predictionsByUser.entrySet()) {
            User user = entry.getKey();
            List<Prediction> userPredictions = entry.getValue();

            int totalTrial = userPredictions.size();
            int totalSuccess = (int) userPredictions.stream().filter(Prediction::getIsCorrect).count();
            int totalFailure = totalTrial - totalSuccess;

            // User의 Record 가져오기
            Record record = recordRepository.findByUser(user);

            if (record == null) {
                // Record 없으면 새로 생성
                record = Record.builder()
                        .user(user)
                        .trial(totalTrial)
                        .success(totalSuccess)
                        .failure(totalFailure)
                        .build();
            } else {
                // Record 있으면 값 갱신
                record.setTrial(totalTrial);
                record.setSuccess(totalSuccess);
                record.setFailure(totalFailure);
            }

            recordRepository.save(record);
        }

        log.info("전체 유저 Record 재계산 및 업데이트 완료");
    }

}
