package com.BitOracle.BitOracle.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//댓글 저장 응답 dto( 대댓글아님 )
@Getter
@Setter
@Builder
public class ReplyResDto {
    private Long replyId;
    private String content;
    private String userName; // 또는 userId
    private Long postId;
    private Long parentId;
    private boolean isRemoved;
    private LocalDateTime createdAt;
}
