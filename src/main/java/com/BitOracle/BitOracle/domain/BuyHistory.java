package com.BitOracle.BitOracle.domain;

import com.BitOracle.BitOracle.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BuyHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(nullable = false, name = "quantity", precision = 18, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, name = "price")
    private double price;// 매수 당시 가격

    @ManyToOne
    @JoinColumn(name = "coin_id")
    private Coin coin;
}