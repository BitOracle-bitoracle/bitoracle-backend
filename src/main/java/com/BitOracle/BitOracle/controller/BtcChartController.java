package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.converter.BtcPriceConverter;
import com.BitOracle.BitOracle.domain.BtcPrice;
import com.BitOracle.BitOracle.dto.BtcPriceResponseDto;
import com.BitOracle.BitOracle.response.DataResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Controller
public class BtcChartController {

/*    @MessageMapping("/btc") // 클라이언트에서 /app/btc 로 보낸 메시지를 받음
    @SendTo("/sub/trade") // 이 응답을 구독자에게 broadcast
    public DataResponseDto<String> receiveBtcMessage(String str) {
        System.out.println("받은 메시지: " + str);

        return DataResponseDto.of(str, "받은 메시지를 성공적으로 broadcast 했습니다.");
    }*/
}
