package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.domain.PostImage;
import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.dto.PostResDto;
import com.BitOracle.BitOracle.dto.PostSaveReqDto;
import com.BitOracle.BitOracle.dto.PostSaveResDto;
import com.BitOracle.BitOracle.repository.PostImageRepository;
import com.BitOracle.BitOracle.repository.PostRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket; //버킷이름
    private final AmazonS3 amazonS3;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;

    //일반 게시글 추가
    public PostSaveResDto save(PostSaveReqDto postSaveDto, List<MultipartFile> uploadFiles, User user) {
        //dto->entity
        Post post = postSaveDto.toEntity(user);
        //if 파일 있으면
        if(uploadFiles != null && !uploadFiles.isEmpty()) {
            List<String> fileNames = upload(uploadFiles); //파일 이름 list 리턴
            List<PostImage> postImageList = new ArrayList<>();
            for (String fileName : fileNames) {
                String imageUrl = amazonS3.getUrl(bucket, fileName).toString(); //s3에 저장된 파일 url

                PostImage postImage = PostImage.builder()
                        .imgUrl(imageUrl)
                        .post(post)
                        .build();
                postImageList.add(postImage);
            }
            log.info("postImageList@@@@ :" + postImageList);
            post.setPostImageList(postImageList);
        }

        Post saved = postRepository.save(post);
        return PostSaveResDto.builder()
                .id(saved.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(user.getNickname())
                .build();
    }

    public List<String> upload(List<MultipartFile> multipartFiles){
        List<String> fileNameList = new ArrayList<>();

        //multipartFiles 리스트로 넘어온 파일들을 filenamelist에 추가
        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()){
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
                        //.withCannedAcl(CannedAccessControlList.PublicRead));//s3 업로드 공개읽기 권한 url로 바로 열람가능
            }catch (IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 실패 s3");
            }
            fileNameList.add(fileName);
        });
        return fileNameList; // List파일 리턴
    }

    // 파일명을 난수화하기 위해 UUID 를 활용하여 난수를 돌린다.
    public String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(getFileExtension(fileName)); //uuid.jpg
    }

    //  "."의 존재 유무만 판단
    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일" + fileName + ") 입니다.");
        }
    }

    //파일 삭제
    public void deleteFile(String fileName){
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }


    //list 전체글 조회
    public Page<PostResDto> findAll(Pageable pageable){
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(post -> PostResDto.builder()
                        .id(post.getPostId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .writer(post.getUser().getNickname()) // 작성자 이름
                        .createdAt(post.getCreatedAt())
                        .build());
    }

}
