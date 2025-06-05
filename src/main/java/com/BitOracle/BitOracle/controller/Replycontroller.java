package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.domain.Reply;
import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.dto.ReplyResDto;
import com.BitOracle.BitOracle.dto.ReplySaveReqDto;
import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.repository.UserEntityRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.ReplyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/reply")
@RestController
@RequiredArgsConstructor
public class Replycontroller {
    private final UserRepository userRepository;
    private final UserEntityRepository userEntityRepository;
    private final ReplyService replyService;
    private final JWTUtil jwtUtil;



    //댓글추가
    @PostMapping("/{postId}")
    public DataResponseDto<ReplyResDto> replySave(@PathVariable(name = "postId") Long postId, ReplySaveReqDto replySaveReqDto,
                                                  @RequestHeader("Authorization") String authorization){
        Reply reply = replyService.saveReply(postId, replySaveReqDto,authorization);
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

    // 대댓글 저장
    @PostMapping("/{postId}/{parentId}")
    public DataResponseDto<ReplyResDto> saveReReply(@PathVariable Long postId,
                                         @PathVariable Long parentId,
                                          ReplySaveReqDto replySaveReqDto,
                                                    @RequestHeader("Authorization") String authorization) {
            Reply reply = replyService.saveRe_Reply(postId, parentId, replySaveReqDto, authorization);
            ReplyResDto replyResDto = ReplyResDto.builder()
                    .replyId(reply.getReplyId())
                    .createdAt(reply.getCreatedAt())
                    .content(reply.getReplyContent())
                    .postId(reply.getPost().getPostId())
                    .userName(reply.getUser().getNickname())
                    .parentId(reply.getParent() != null ? reply.getParent().getReplyId() : null)
                    .isRemoved(reply.isRemoved())
                    .build();
            return DataResponseDto.of(replyResDto,"대댓글이 저장되었습니다.");
    }
    @DeleteMapping("/{replyId}")
    public DataResponseDto<?> deleteReply(
                                                    @PathVariable Long replyId
                                                    ,@RequestHeader("Authorization") String authorization) {
        replyService.remove(replyId,authorization);
        return DataResponseDto.of("댓글 삭제 완료");
    }

}
