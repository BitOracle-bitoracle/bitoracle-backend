package com.BitOracle.BitOracle.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BtcPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id")
    private Long priceId;

    @Column(name = "coin_date")
    private Date coinDate;

    @Column(name = "price", precision = 18, scale = 4)
    private BigDecimal price;

    @OneToMany(mappedBy = "btcPrice", cascade = CascadeType.ALL)
    private List<Prediction> predictionList = new ArrayList<>();
}
