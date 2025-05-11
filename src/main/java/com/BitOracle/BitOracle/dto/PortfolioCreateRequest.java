package com.BitOracle.BitOracle.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PortfolioCreateRequest {
    private String coinName;
    private List<BuyHistoryCreateRequest> buyHistories;
}