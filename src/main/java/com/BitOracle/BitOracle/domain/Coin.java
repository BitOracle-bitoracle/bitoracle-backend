package com.BitOracle.BitOracle.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coin_id")
    private Long coinId;
    @Column(nullable = false, name = "coin_name")
    private String coinName;
    @Column(nullable = false, name = "quantity", precision = 18, scale = 8)
    private BigDecimal quantity;

    // 포폴쪽이 확정되지 않아서 아직
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Portfolio portfolio;
}
