package com.BitOracle.BitOracle.controller;

import com.BitOracle.BitOracle.converter.PredictionConverter;
import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.domain.Prediction;
import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.domain.enums.PostType;
import com.BitOracle.BitOracle.domain.enums.UserType;
import com.BitOracle.BitOracle.dto.*;
import com.BitOracle.BitOracle.dummy.PostSearchCondition;
import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.repository.LikeRepository;
import com.BitOracle.BitOracle.repository.UserEntityRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import com.BitOracle.BitOracle.response.DataResponseDto;
import com.BitOracle.BitOracle.service.PostService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class PostController {
    private final JWTUtil jwtUtil;
    private final PostService postService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final UserEntityRepository userEntityRepository;;

    @GetMapping("/my-posts")
    public List<PostResDto> getMyPosts(@CookieValue("access") String authorization) {
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        User userEnt = userEntity.getUser();
        Long userId = userEnt.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        log.info("사용자 정보 :" +  user.getNickname());
        return postService.findAllByUser(user);
    }

    @PostMapping(value = "/user")
    public void selectUpDown(@CookieValue("access") String authorization){
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        User userEnt = userEntity.getUser();
        Long userId = userEnt.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        log.info("사용자 정보 :" +  user.getNickname());
    }
    @PostMapping(value = "/post/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataResponseDto<String> uploadImage(@RequestPart("image") MultipartFile image) {
        String imageUrl = postService.saveImage(image);
        return DataResponseDto.of(imageUrl, "이미지 업로드 성공");
    }

    @PostMapping(value = "/post",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataResponseDto<PostSaveResDto> savePost(@RequestPart(value = "post") @Parameter(schema =@Schema(type = "string", format = "binary")) PostSaveReqDto postSaveReqDto,
                                                    @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                                    @CookieValue("access") String authorization
                                                    )
    {
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        User userEnt = userEntity.getUser();
        Long userId = userEnt.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        log.info("현재 사용자 : " + user.getNickname());
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
    //인기글 전체 조회
    @GetMapping("/popular")
    public DataResponseDto<Page<PostResDto>> getPopularPosts(@RequestParam(defaultValue = "0", name = "page") int page,
                                                             @RequestParam(defaultValue = "10", name = "size") int size,
                                                             @RequestParam(defaultValue = "createdAt", name = "sortBy") String sortBy,
                                                             @RequestParam(defaultValue = "desc", name = "direction") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PostResDto> posts = postService.findByPostType(PostType.POPULAR, pageable);
        return DataResponseDto.of(posts, "인기 글 조회");
    }
    //칼럼 전체조회
    @GetMapping("/column")
    public DataResponseDto<Page<ColumnResDto>> getAllColumns(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "createdAt") String sortBy,
                                                           @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ColumnResDto> posts = postService.findAllColumns(pageable);
        return DataResponseDto.of(posts, "칼럼글 조회 성공");
    }
    //단건조회
    @GetMapping("/post/{postId}")
    public DataResponseDto<PostInfoDto> getPostInfo(@PathVariable("postId")Long postId) {
        return DataResponseDto.of(postService.getPostInfo(postId));
    }

    //게시글 검색 제목 or 작성자
    @GetMapping("/search")
    public DataResponseDto<PostPagingDto> search(Pageable pageable,
                                    @ModelAttribute PostSearchCondition postSearchCondition){
        return DataResponseDto.of(postService.getSearchPostList(pageable,postSearchCondition));
    }


    @PostMapping("/{postId}/like")
    public DataResponseDto<LikeRes> like(@PathVariable Long postId, @CookieValue("access") String authorization){
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        User userEnt = userEntity.getUser();
        Long userId = userEnt.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        log.info("현재 사용자 : " + user.getNickname());


        boolean islike = postService.likePost(user, postId);
        long likeCount = postService.getLikeCount(postId);
        return DataResponseDto.of(new LikeRes(islike,likeCount));
    }

    //삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        @CookieValue("access") String authorization) {
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        User userEnt = userEntity.getUser();
        Long userId = userEnt.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        postService.deletePost(postId, user);
        return ResponseEntity.ok().body("게시글이 삭제되었습니다.");
    }
//수정
    @PostMapping("/{postId}")
    public  DataResponseDto<PostSaveResDto> updatePost(
            @PathVariable Long postId,
            @RequestPart(value = "post") @Parameter(schema =@Schema(type = "string", format = "binary")) PostSaveReqDto postSaveReqDto,
                @RequestPart(value = "images",required = false) List<MultipartFile> images,
                @CookieValue("access") String authorization
                    ) {
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        User userEnt = userEntity.getUser();
        Long userId = userEnt.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        PostSaveResDto res = postService.updatePost(postId, postSaveReqDto, images, user);
        return DataResponseDto.of(res,"게시글 수정되었습니다.");
    }



}
