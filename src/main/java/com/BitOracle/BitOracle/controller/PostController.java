package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.domain.enums.UserType;
import com.BitOracle.BitOracle.dto.*;
import com.BitOracle.BitOracle.repository.UserRepository;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.PostService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;

    @GetMapping("/api/user/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "roles", userDetails.getAuthorities()
        ));
    }
    @PostMapping(value = "/post",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataResponseDto<PostSaveResDto> savePost(@RequestPart(value = "post") @Parameter(schema =@Schema(type = "string", format = "binary")) PostSaveReqDto postSaveReqDto,
                                                    @RequestPart(value = "images",required = false) List<MultipartFile> images
                                                    )
    {
        User user = User.builder()
                .nickname("jinseo")
                .userType(UserType.USER)
                .point(1)
                .build();
        userRepository.save(user);
        PostSaveResDto resDto = postService.save(postSaveReqDto, images, user);
        return DataResponseDto.of(resDto,"게시글 등록되었습니다.");
    }

    @GetMapping
    public DataResponseDto<Page<PostResDto>> getAllPosts(@RequestParam(defaultValue = "0",name = "page")int page,
                                                         @RequestParam(defaultValue = "10",name = "size") int size,
                                                         @RequestParam(defaultValue = "createdAt",name="sortBy") String sortBy,
                                                         @RequestParam(defaultValue = "desc",name="direction") String direction){
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PostResDto> posts = postService.findAll(pageable);
        return DataResponseDto.of(posts,"전체 글 조회@@");
    }
    //단건조회
    @GetMapping("/post/{postId}")
    public DataResponseDto<PostInfoDto> getPostInfo(@PathVariable("postId")Long postId) {
        return DataResponseDto.of(postService.getPostInfo(postId));
    }

    //댓글 추가
    //@PostMapping("/post/{postId}/comment")


}
