package com.BitOracle.BitOracle.domain;

import com.BitOracle.BitOracle.common.BaseEntity;
import com.BitOracle.BitOracle.domain.enums.PostType;
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
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;
    @Column(nullable = false, name = "content")
    private String content;
    @Column(nullable = false, name = "title")
    private String title;
    @Column(name = "like_count")
    private int likeCount = 0;
    @Column(name = "post_type")
    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Column(nullable = true)
    private String thumbnailUrl; // 썸네일 이미지 URL

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Reply> replyList = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostImage> postImageList = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Likes> likeList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;



    public void addReply(Reply comment){
        //comment의 Post 설정은 comment에서 함
        replyList.add(comment);
    }

    //== 연관관계 편의 메서드 ==//
    public void confirmWriter(User writer) {
        //writer는 변경이 불가능하므로 이렇게만 해주어도 될듯
        this.user = writer;
        writer.addPost(this);
    }
    public void addComment(Reply comment){
        //comment의 Post 설정은 comment에서 함
        replyList.add(comment);
    }

}
