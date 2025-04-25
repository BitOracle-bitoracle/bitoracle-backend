package com.BitOracle.BitOracle.domain;

import com.BitOracle.BitOracle.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(nullable = false, name = "user_name")
    private String userName;

    @Column(nullable = false, name = "point")
    private Integer point;
//    @Column(nullable = false, name = "user_email")
//    private String userEmail;
//    @Column(nullable = false, name = "google_id")
//    private String googleId;
//    @Column(nullable = false, name = "img_url")
//    private String imgUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Reply> replyList = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> postList = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Likes> likeList = new ArrayList<>();
    // 포폴 쪽은 아직 추가 안함
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Prediction> predictionList = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Record record;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Portfolio portfolio;
}
