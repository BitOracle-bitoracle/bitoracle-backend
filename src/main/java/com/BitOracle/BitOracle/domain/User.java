package com.BitOracle.BitOracle.domain;

import com.BitOracle.BitOracle.common.BaseEntity;
import com.BitOracle.BitOracle.domain.enums.UserType;
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

    @Column(nullable = false)
    private String nickname;

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(nullable = false, name = "point")
    private Integer point;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Reply> replyList = new ArrayList<>();
    //==회원탈퇴 -> 작성 게시물, 댓글 모두 삭제 == //
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likeList = new ArrayList<>();
    // 포폴 쪽은 아직 추가 안함
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Prediction> predictionList = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Record record;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Portfolio portfolio;

    public void addReply(Reply reply){
        //comment의 writer 설정은 comment에서 함
        replyList.add(reply);
    }


    //== 연관관계 메서드 ==//
    public void addPost(Post post){
        //post의 writer 설정은 post에서 함
        postList.add(post);
    }

    public void addComment(Reply comment){
        //comment의 writer 설정은 comment에서 함
        replyList.add(comment);
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
        if (portfolio.getUser() != this) {
            portfolio.setUser(this);
        }
    }


}
