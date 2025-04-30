package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.domain.Reply;
import com.BitOracle.BitOracle.dto.ReplyResDto;
import com.BitOracle.BitOracle.dto.ReplySaveReqDto;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/reply")
@RestController
@RequiredArgsConstructor
public class Replycontroller {
    private final ReplyService replyService;

    @PostMapping("/{postId}")
    public DataResponseDto<ReplyResDto> replySave(@PathVariable(name = "postId") Long postId, ReplySaveReqDto replySaveReqDto){
        Reply reply = replyService.saveReply(postId, replySaveReqDto);
        ReplyResDto replyResDto = ReplyResDto.builder()
                .replyId(reply.getReplyId())
                .createdAt(reply.getCreatedAt())
                .content(reply.getReplyContent())
                .postId(reply.getPost().getPostId())
                .userName(reply.getUser().getNickname())
                .parentId(reply.getParent() != null ? reply.getParent().getReplyId() : null)
                .isRemoved(reply.isRemoved())
                .build();
        return DataResponseDto.of(replyResDto,"댓글이 저장되었습니다.");
    }
}
