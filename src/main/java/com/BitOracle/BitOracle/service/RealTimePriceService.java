package com.BitOracle.BitOracle.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RealTimePriceService {
    private final ConcurrentHashMap<String,Double> priceMap = new ConcurrentHashMap<>();

    public void updatePrice(String coinName, double price) {
        //log.info("Updating price for @@@@@@@@@@" + price);
        priceMap.put(coinName, price);
    }
    public double getPrice(String coinName) {
        return priceMap.getOrDefault(coinName, 0.0);
    }

}
