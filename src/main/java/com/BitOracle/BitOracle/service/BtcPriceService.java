package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.BtcPrice;
import com.BitOracle.BitOracle.repository.BtcPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class BtcPriceService {
    private final BtcPriceRepository btcPriceRepository;

    public BtcPrice getMidnightBtc(){
        LocalDate today = LocalDate.now();
        return btcPriceRepository.findByCoinDate(today);
    }
}
