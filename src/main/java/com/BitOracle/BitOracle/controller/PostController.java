package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.dto.PostSaveDto;
import com.BitOracle.BitOracle.repository.UserRepository;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.PostService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;

    @PostMapping(value = "/post",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataResponseDto<PostSaveDto> savePost(@RequestPart(value = "post") @Parameter(schema =@Schema(type = "string", format = "binary"))PostSaveDto postSaveDto, @RequestPart(value = "images",required = false) List<MultipartFile> images)
    {
        User user = User.builder()
                .userName("test")
                .point(0)
                .build();
        userRepository.save(user);
        postService.save(postSaveDto,images,user);
        return DataResponseDto.of(postSaveDto,"게시글 등록되었습니다.");
    }
}
