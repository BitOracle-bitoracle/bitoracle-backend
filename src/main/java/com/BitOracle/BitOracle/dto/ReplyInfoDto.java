package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.domain.Reply;
import lombok.Data;

import java.util.List;

@Data
public class ReplyInfoDto {
    private final static String DEFAULT_DELETE_MESSAGE = "삭제된 댓글입니다";

    private Long postId;
    private Long replyId;
    private String content; //내용
    private boolean isRemoved;
    private MemberDto writerDto;//댓글 작성자 정보
    private List<ReCommentInfoDto> re_ReplyListDtoList;


    public ReplyInfoDto(Reply comment, List<Reply> reCommentList) {
        this.postId = comment.getPost().getPostId();
        this.replyId = comment.getReplyId();
        this.content = comment.getReplyContent();
        if(comment.isRemoved()){
            this.content = DEFAULT_DELETE_MESSAGE;
        }
        this.isRemoved = comment.isRemoved();
        this.writerDto = new MemberDto(comment.getUser());
        this.re_ReplyListDtoList = reCommentList.stream().map(ReCommentInfoDto::new).toList(); //대댓글 그룹
    }
}
