package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.Prediction;
import com.BitOracle.BitOracle.domain.Record;
import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.dto.PredictionRequestDto;
import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.repository.PredictionRepository;
import com.BitOracle.BitOracle.repository.RecordRepository;
import com.BitOracle.BitOracle.repository.UserEntityRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final JWTUtil jwtUtil;
    private final UserEntityRepository userEntityRepository;
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;

    public Prediction selectUpDown(String authorization, PredictionRequestDto.PredictionUpDownRequestDto predictionUpDownRequestDto){
        Long userId = getUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        log.info(user.toString());
        Prediction prediction = Prediction.builder()
                .user(user)
                .upDown(predictionUpDownRequestDto.getUpDown())
                .build();

        return predictionRepository.save(prediction);
    }

    public List<Prediction> getCalendar(String authorization){
        Long userId = getUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        log.info(user.toString());

        return predictionRepository.findByUser(user);
    }

    public Record getStats(String authorization){
        Long userId = getUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        log.info(user.toString());

        return recordRepository.findByUser(user);
    }

    public Prediction checkTodayPrediction(String authorization) {
        Long userId = getUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);

        return predictionRepository.findByUserAndCreatedAtBetween(user, startOfToday, endOfToday).orElse(null);
    }

    public Long getUserId(String authorization){
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        User user = userEntity.getUser();
        return user.getUserId();
    }
}
