package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.domain.Reply;
import com.BitOracle.BitOracle.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ReplySaveReqDto {
    @NotBlank(message = "내용을 입력해주세요")
    private String content;
    //private Long postId;

    public Reply toEntity() {
        return Reply.builder()
                .replyContent(this.content)
                .build();
    }
}
