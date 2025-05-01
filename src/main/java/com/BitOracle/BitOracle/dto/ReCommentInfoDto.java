package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.domain.Reply;
import lombok.Data;

@Data
public class ReCommentInfoDto {
    private final static String DEFAULT_DELETE_MESSAGE = "삭제된 댓글입니다";

    private Long postId;
    private Long parentId;
    private Long reCommentId;
    private String content;
    private boolean isRemoved;

    private MemberDto writerDto;

    public ReCommentInfoDto(Reply reComment){
        this.postId = reComment.getPost().getPostId();
        this.parentId = reComment.getParent().getReplyId();
        this.reCommentId = reComment.getReplyId();
        this.content = reComment.getReplyContent();
        if(reComment.isRemoved()){
            this.content = DEFAULT_DELETE_MESSAGE;
        }
        this.isRemoved = reComment.isRemoved();
        this.writerDto = new MemberDto(reComment.getUser());
    }
}
