package com.BitOracle.BitOracle.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class CoinMarketCapResponse {
    private Map<String, CryptoData> data;

    @Getter
    public static class CryptoData {
        private Map<String, Quote> quote;
    }

    @Getter
    public static class Quote {
        private String price;
    }
}
