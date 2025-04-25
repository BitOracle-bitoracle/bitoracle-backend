package com.BitOracle.BitOracle.domain;

import com.BitOracle.BitOracle.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BtcPrice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id")
    private Long priceId;

    @Column(name = "coin_date")
    private LocalDate coinDate;

    @Column(name = "price", precision = 18, scale = 4)
    private BigDecimal price;
}
