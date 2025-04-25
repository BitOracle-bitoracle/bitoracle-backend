package com.BitOracle.BitOracle.domain;

import com.BitOracle.BitOracle.common.BaseEntity;
import com.BitOracle.BitOracle.domain.enums.PostType;
import com.BitOracle.BitOracle.domain.enums.PredictType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Prediction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "predict_id")
    private Long predictId;

    @Column(name = "predict_time")
    private Date predictTime;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "up_down")
    private Enum UpDown;

    @Column(name = "predict_type")
    @Enumerated(EnumType.STRING)
    private PredictType predictType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
