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

    private List<ReplyInfoDto> replyList;
    private List<PostImage> postImageList; //이미지 리스트

    public PostInfoDto(Post post){
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCount = post.getLikeCount();
        this.postImageList = post.getPostImageList();

        this.writer = new MemberDto(post.getUser());

        List<Reply> replies = post.getReplyList();

        //부모댓글리스트
        List<Reply> parentReplies = replies.stream()
                .filter(reply -> reply.getParent() == null)
                .toList();
        //댓글 대댓글 그룹짖기 post.getCommentList는 댓글과 대댓글 모두 조회 섞여잇는상태
        //Map<댓글, List<해당 댓글에 달린 대댓글>>의 형식
        Map<Reply, List<Reply>> replyListMap = replies.stream()
                .filter(reply -> reply.getParent() != null)//대댓글만 선별
                .collect(Collectors.groupingBy(Reply::getParent));

        //댓글과 대댓글로 replyList생성
        replyList = parentReplies.stream()//댓글가지고오기
                .map(reply -> new ReplyInfoDto(reply,replyListMap.getOrDefault(reply, List.of())))
                .toList();

    }
}
