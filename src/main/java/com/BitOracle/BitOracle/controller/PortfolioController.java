package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.dto.BuyHistoryCreateRequest;
import com.BitOracle.BitOracle.dto.PortfolioCreateRequest;
import com.BitOracle.BitOracle.dto.PortfolioResDto;
import com.BitOracle.BitOracle.dto.SellRequest;
import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.repository.UserEntityRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import com.BitOracle.BitOracle.service.PortfolioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @PostMapping("/buy")
    public ResponseEntity<String> buyCoin(
            @RequestHeader("Authorization") String authorization,
            @RequestBody BuyHistoryCreateRequest request
    ) {
        Long userId = portfolioService.getUserId(authorization);
        portfolioService.buyCoin(userId, request);
        return ResponseEntity.ok("매수 완료");
    }

    // 매도 요청
    @PostMapping("/sell")
    public ResponseEntity<?> sellCoin(@RequestHeader("Authorization") String authorization,
                                      @RequestBody SellRequest request) {
        Long userId = portfolioService.getUserId(authorization);
        try {
            portfolioService.sellCoin(userId, request);
            return ResponseEntity.ok("코인 매도 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("매도 실패: " + e.getMessage());
        }
    }
}
