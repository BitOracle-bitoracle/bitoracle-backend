package com.BitOracle.BitOracle.domain;

import com.BitOracle.BitOracle.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coin_id")
    private Long coinId;

    @Column(nullable = false, name = "coin_name")
    private String coinName; //btc eth 등
/*    @Column(nullable = false, name = "quantity", precision = 18, scale = 8)
    private BigDecimal quantity;*/

    // 포폴쪽이 확정되지 않아서 아직
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Portfolio portfolio;

    @OneToMany(mappedBy = "coin", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BuyHistory>  buyHistories  = new ArrayList<>();
}
