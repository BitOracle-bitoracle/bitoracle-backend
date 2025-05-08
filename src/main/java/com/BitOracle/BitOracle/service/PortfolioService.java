package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.*;
import com.BitOracle.BitOracle.dto.PortfolioResDto;
import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.repository.CoinRepoistory;
import com.BitOracle.BitOracle.repository.UserEntityRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
//실시간 변동값
//currentPrice
//
//evaluationAmount
//
//profitRate
@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final RealTimePriceService realTimePriceService;
    private final UserRepository userRepository;
    private final UserEntityRepository userEntityRepository;
    private final CoinRepoistory  coinRepoistory;
    private final JWTUtil jwtUtil;

    /// 포폴 조회
    @Transactional(readOnly = true)
    public List<PortfolioResDto>   getPortfolio(String authorization) {
        Long userId = getUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Portfolio portfolio = user.getPortfolio();
        List<PortfolioResDto> response = new ArrayList<>();

        for (Coin coin : portfolio.getCoinList()) {
            List<BuyHistory> histories = coin.getBuyHistories(); //구매내역 리스트

            // 보유 수량 (정밀도 필요)만 BigDecimal 유지
            BigDecimal totalQuantity = histories.stream()
                    .map(BuyHistory::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add); //보유 전체 수량

            // 매수 금액 (double로 계산) 구매했을때 가격 x 몇개 구매햇는지
            double totalBuyAmount = histories.stream()
                    .mapToDouble(h -> h.getPrice() * h.getQuantity().doubleValue())
                    .sum();

            //평균 매수 가격 매수금액 / 매수양
            double averageBuyPrice = totalQuantity.compareTo(BigDecimal.ZERO) == 0 ?
                    0.0 : totalBuyAmount / totalQuantity.doubleValue();

            double currentPrice = realTimePriceService.getPrice(coin.getCoinName());

            //평가금맥  매수양 x 현재가격 정의: 내가 보유한 코인의 현재 시세 기준 자산 가치를 의미합니다.
            double evaluationAmount = totalQuantity.multiply(BigDecimal.valueOf(currentPrice)).doubleValue();

            //평가 순익   (평가금액-매수양) / 매수양 * 100
            double profitRate = totalBuyAmount == 0 ? 0.0 :
                    (evaluationAmount - totalBuyAmount) / totalBuyAmount * 100;

            response.add(PortfolioResDto.builder()
                            .coinName(coin.getCoinName())
                            .quantity(totalQuantity)
                            .averageBuyPrice(averageBuyPrice)
                            .currentPrice(currentPrice)
                            .evaluatuonAmount(evaluationAmount)
                            .profitRate(profitRate)
                            .totalBuyAmount(totalQuantity)
                            .build()
            );
        }
        return response;
    }

    public Long getUserId(String authorization){
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        User user = userEntity.getUser();
        return user.getUserId();
    }
}
