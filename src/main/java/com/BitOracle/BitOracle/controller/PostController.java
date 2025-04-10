package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.dto.PostResDto;
import com.BitOracle.BitOracle.dto.PostSaveReqDto;
import com.BitOracle.BitOracle.dto.PostSaveResDto;
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
    public DataResponseDto<PostSaveResDto> savePost(@RequestPart(value = "post") @Parameter(schema =@Schema(type = "string", format = "binary")) PostSaveReqDto postSaveReqDto, @RequestPart(value = "images",required = false) List<MultipartFile> images)
    {
        User user = User.builder()
                .userName("진서")
                .point(1)
                .build();
        userRepository.save(user);
        PostSaveResDto resDto = postService.save(postSaveReqDto, images, user);
        return DataResponseDto.of(resDto,"게시글 등록되었습니다.");
    }

    @GetMapping
    public DataResponseDto<List<PostResDto>> getAllPosts(){
        List<PostResDto> posts = postService.findAll();
        return DataResponseDto.of(posts,"전체 글 조회@@");
    }
}
