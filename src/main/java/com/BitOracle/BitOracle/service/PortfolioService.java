package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.*;
import com.BitOracle.BitOracle.dto.BuyHistoryCreateRequest;
import com.BitOracle.BitOracle.dto.PortfolioCreateRequest;
import com.BitOracle.BitOracle.dto.PortfolioResDto;
import com.BitOracle.BitOracle.dto.SellRequest;
import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.repository.CoinRepoistory;
import com.BitOracle.BitOracle.repository.PortfolioRepository;
import com.BitOracle.BitOracle.repository.UserEntityRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
//실시간 변동값
//currentPrice
//
//evaluationAmount
//
//profitRate

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final RealTimePriceService realTimePriceService;
    private final UserRepository userRepository;
    private final UserEntityRepository userEntityRepository;
    private final CoinRepoistory  coinRepoistory;
    private final JWTUtil jwtUtil;
    private final PortfolioRepository portfolioRepository;
    private final ApplicationContext applicationContext;


    /// 포폴 조회
    @Transactional(readOnly = true)
    public List<PortfolioResDto>   getPortfolio(Long userId) {
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
           // log.info("totalBuyAmouyn@@@@"+totalBuyAmount);
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
                            .totalBuyAmount(totalBuyAmount)
                            .build()
            );
        }
        return response;
    }



    //포폴 생성
    @Transactional
    public void createPortfolio(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 포트폴리오 없으면 생성
        Portfolio portfolio = user.getPortfolio();
        if (portfolio == null) {
            portfolio = Portfolio.builder()
                    .user(user)
                    .build();

        }

        portfolioRepository.save(portfolio); // cascade 설정 필요
    }

    @Transactional
    public void buyCoin(Long userId, BuyHistoryCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 포트폴리오가 없다면 생성
        Portfolio portfolio = user.getPortfolio();

        // coinName으로 Coin 찾기 (없으면 생성)
        Coin coin = portfolio.getCoinList().stream()
                .filter(c -> c.getCoinName().equalsIgnoreCase(request.getCoinName()))
                .findFirst()
                .orElseGet(() -> {
                    Coin newCoin = Coin.builder()
                            .coinName(request.getCoinName())
                            .portfolio(portfolio)
                            .buyHistories(new ArrayList<>())
                            .build();
                    portfolio.getCoinList().add(newCoin);
                    return newCoin;
                });

        // 현재 가격 조회
        double buyPrice = (request.getPrice() != null) ? request.getPrice() : realTimePriceService.getPrice(request.getCoinName());

        // BuyHistory 생성
        BuyHistory buyHistory = BuyHistory.builder()
                .coin(coin)
                .quantity(request.getQuantity())
                .price(buyPrice)
                .build();
        coin.getBuyHistories().add(buyHistory);

        // 저장 (Cascade 설정 전제)
        portfolioRepository.save(portfolio);
    }

    //코인 매도
    @Transactional
    public void sellCoin(Long userId, SellRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Portfolio portfolio = user.getPortfolio();
        Coin coin = portfolio.getCoinList().stream()
                .filter(c -> c.getCoinName().equalsIgnoreCase(request.getCoinName()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("해당 코인을 보유하고 있지 않습니다."));

        BigDecimal remainingSellQty = request.getQuantity();
        double currentPrice = (request.getPrice() != null) ? request.getPrice() : realTimePriceService.getPrice(request.getCoinName());
        log.info("current price: " + currentPrice);
        double sellAmount = currentPrice * remainingSellQty.doubleValue();

        // 매도된 매수단가 총합
        double totalBuyCost = 0.0;

        List<BuyHistory> buyHistories = coin.getBuyHistories();
        buyHistories.sort(Comparator.comparing(BuyHistory::getHistoryId)); // FIFO

        for (BuyHistory history : new ArrayList<>(buyHistories)) {
            if (remainingSellQty.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal historyQty = history.getQuantity();

            // 매도 수량이 더 많으면 전부 소비
            if (remainingSellQty.compareTo(historyQty) >= 0) {
                totalBuyCost += historyQty.doubleValue() * history.getPrice();
                remainingSellQty = remainingSellQty.subtract(historyQty);
                buyHistories.remove(history);
            } else {
                // 일부만 소비
                totalBuyCost += remainingSellQty.doubleValue() * history.getPrice();
                history.setQuantity(historyQty.subtract(remainingSellQty));
                remainingSellQty = BigDecimal.ZERO;
            }
        }

        if (remainingSellQty.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("보유 수량보다 많은 수량을 매도할 수 없습니다.");
        }

        // 손익 및 손익률 계산
        double profit = sellAmount - totalBuyCost;
        double profitRate = totalBuyCost == 0.0 ? 0.0 : (profit / totalBuyCost) * 100;

        log.info("매도 수익: {}원, 손익률: {}%", profit, profitRate);

        portfolioRepository.save(portfolio); // 변경사항 저장
    }

    public Long getUserId(String authorization){
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        User user = userEntity.getUser();
        return user.getUserId();
    }
}
