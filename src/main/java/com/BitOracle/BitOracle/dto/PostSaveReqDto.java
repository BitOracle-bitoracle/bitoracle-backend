package com.BitOracle.BitOracle.dto;

import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PostSaveReqDto {
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    @NotBlank(message = "내용을 입력해주세요")
    private String content;
    @NotBlank(message = "게시글 유형 입력해")
    private PostType postType;

    public Post toEntity(User user) {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .postType(this.postType)
                .user(user)
                .build();
    }


}
