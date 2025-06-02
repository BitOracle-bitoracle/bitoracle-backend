package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.domain.PostImage;
import com.BitOracle.BitOracle.domain.Reply;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class PostInfoDto {
    private Long postId;
    private int likeCount;
    private String title;
    private String content;
    private MemberDto writer;
    private LocalDateTime createdAt;

    private List<String> postImageList; //이미지 리스트
    private List<ReplyInfoDto> replyList;
    public PostInfoDto(Post post){
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCount = post.getLikeCount();
        this.createdAt = post.getCreatedAt();
        this.postImageList = post.getPostImageList().stream()
                .map(PostImage :: getImgUrl)
                .collect(Collectors.toList());

        this.writer = new MemberDto(post.getUser());

        //댓글 대댓글 그룹짖기 post.getCommentList는 댓글과 대댓글 모두 조회 섞여잇는상태
        Map<Reply, List<Reply>> replyListMap = post.getReplyList().stream()
                .filter(reply -> reply.getParent() != null)//대댓글만 선별
                .collect(Collectors.groupingBy(Reply::getParent));
        // 부모 댓글(댓글)만 따로 필터링
        this.replyList = post.getReplyList().stream()
                .filter(reply -> reply.getParent() == null) // 댓글만
                .map(reply -> new ReplyInfoDto(reply, replyListMap.getOrDefault(reply, List.of())))
                .toList();

        //댓글과 대댓글로 replyList생성
/*        replyList = replyListMap.keySet().stream()//댓글가지고오기
                .map(reply -> new ReplyInfoDto(reply,replyListMap.get(reply)))
                .toList();*/

    }
}
