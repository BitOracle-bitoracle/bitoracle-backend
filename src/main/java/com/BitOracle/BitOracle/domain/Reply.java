package com.BitOracle.BitOracle.domain;

import com.BitOracle.BitOracle.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long replyId;

    @Column(name = "reply_content")
    private String replyContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isRemoved = false; //삭제 된 댓글인지 아닌지 대댓글을 위해 파악 필요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 대댓글 구현을 위한 자기 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Reply parent;

    @OneToMany(mappedBy = "parent" , cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Reply> childList = new ArrayList<>();

    //연관관계 편의 메서드
    public void setUser(User user) {
        this.user = user;
        user.addReply(this);
    }
    public void setPost(Post post) {
        this.post = post;
        post.addReply(this);
    }
    public void setParent(Reply parent) {
        this.parent = parent;
        parent.addChild(this);
    }
    public void addChild(Reply child) {
        childList.add(child);
    }

    // 수정 구현 x
    public void updateContent(String content){
        this.replyContent = content;
    }
    //삭제
    public void removeReply(){
        this.isRemoved = true; // 실제db 삭제 x boolean값만 true로
    }

    //비즈니스 로직
    public List<Reply> findRemovableList(){
        List<Reply> result = new ArrayList<>();

        Optional.ofNullable(this.parent).ifPresentOrElse( //this parent가 null이 아니면 첫번째 람다 ifpresent, null이면 두번째 람다실행
                parentReply ->{//대댓글인 경우 (부모가 존재하는 경우)
                    if( parentReply.isRemoved()&& parentReply.isAllChildRemoved()){ //다 삭제 가능 부모 대댓글 싹다 삭제된 상태
                        result.addAll(parentReply.getChildList()); //자식모두 추가
                        result.add(parentReply); //부모 추가
                    }
                },

                () -> {//댓글인 경우(부모가 null임)
                    if (isAllChildRemoved()) {
                        result.add(this); //자기자신
                        result.addAll(this.getChildList());
                    }
                }
        );
        return result;
    }
    //모든 자식 댓글이 삭제되었는지 판단
    private boolean isAllChildRemoved() {
        return getChildList().stream()
                .map(Reply::isRemoved)//지워졌는지 여부로 바꾼다
                .filter(isRemove -> !isRemove)//지워졌으면 true, 안지워졌으면 false이다. 따라서 filter에 걸러지는 것은 false인 녀석들이고, 있다면 false를 없다면 orElse를 통해 true를 반환한다.
                .findAny()//지워지지 않은게 하나라도 있다면 false를 반환
                .orElse(true);//모두 지워졌다면 true를 반환
    }

}
