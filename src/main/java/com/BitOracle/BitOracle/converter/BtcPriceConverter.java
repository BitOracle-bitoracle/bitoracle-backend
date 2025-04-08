package com.BitOracle.BitOracle.converter;

import com.BitOracle.BitOracle.domain.BtcPrice;
import com.BitOracle.BitOracle.dto.BtcPriceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BtcPriceConverter {

    public static BtcPriceResponseDto.BtcPriceMidnightResponseDto toBtcPriceMidnightResponseDto (BtcPrice btcPrice){
        return BtcPriceResponseDto.BtcPriceMidnightResponseDto.builder()
                .today(btcPrice.getCoinDate())
                .price(btcPrice.getPrice())
                .build();
    }
}
