package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.converter.BtcPriceConverter;
import com.BitOracle.BitOracle.domain.BtcPrice;
import com.BitOracle.BitOracle.dto.BtcPriceResponseDto;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.BtcPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class BtcPriceController {
    private final BtcPriceService btcPriceService;

    @GetMapping(value = "/predict/midnight")
    public DataResponseDto<BtcPriceResponseDto.BtcPriceMidnightResponseDto> getMidnightBtc(){
        BtcPrice btcPrice = btcPriceService.getMidnightBtc();
        BtcPriceResponseDto.BtcPriceMidnightResponseDto responseDto = BtcPriceConverter.toBtcPriceMidnightResponseDto(btcPrice);
        return DataResponseDto.of(responseDto, "금일 00시 비트코인 기준가를 조회했습니다.");
    }
}
