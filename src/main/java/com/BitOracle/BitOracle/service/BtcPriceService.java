package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.BtcPrice;
import com.BitOracle.BitOracle.repository.BtcPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Slf4j
public class BtcPriceService {
    private final BtcPriceRepository btcPriceRepository;

    public BtcPrice getMidnightBtc(){
        LocalDate today = LocalDate.now();
        log.info("Today is {}", today);
        return btcPriceRepository.findByCoinDate(today);
    }
}
