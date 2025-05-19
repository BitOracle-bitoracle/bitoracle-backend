package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.domain.Portfolio;
import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.dto.PortfolioResDto;
import com.BitOracle.BitOracle.repository.UserEntityRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import com.BitOracle.BitOracle.service.PortfolioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PortfolioWebSocketController {
    private final PortfolioService portfolioService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry simpUserRegistry;
    private final UserEntityRepository userEntityRepository;
    private final UserRepository userRepository;


    //연결된 사용자 목록
    private final Map<String,String> activeUsers = new ConcurrentHashMap<>();


    //3초마다 포트폴리오 푸시
    @Scheduled(fixedRate = 5000)
    public void startPortfolioUpdates() {
        //log.info("username @@@@@@@@@@@@@@@@@@@@@@@@@소켓유저 {}", simpUserRegistry.getUsers());
        simpUserRegistry.getUsers().forEach(user -> {
            String username = user.getName();
            log.info("username @@@@@@@@@@@@@@@@@@@@@@@@@유저네임유저네임 {}", username);
            UserEntity userEntity = userEntityRepository.findByName(username);
            User userEnt = userEntity.getUser();
            Long userId = userEnt.getUserId();
            User realuser = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
            log.info("realuser @@@@@@@@ {}", realuser);
            List<PortfolioResDto> portfolio = portfolioService.getPortfolio(userId); //포트폴리오
            log.info("Portfolio for user {}: {}", userId, portfolio);
            messagingTemplate.convertAndSendToUser(username, "/queue/portfolio", portfolio);
        });
    }
}
