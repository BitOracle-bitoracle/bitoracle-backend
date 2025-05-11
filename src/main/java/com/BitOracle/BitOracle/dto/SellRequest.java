package com.BitOracle.BitOracle.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SellRequest {
    private String coinName;
    private BigDecimal quantity;
}