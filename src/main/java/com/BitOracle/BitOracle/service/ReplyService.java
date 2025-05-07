package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.Reply;
import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.UserEntity;
import com.BitOracle.BitOracle.dto.ReplySaveReqDto;
import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.repository.PostRepository;
import com.BitOracle.BitOracle.repository.ReplyRepository;
import com.BitOracle.BitOracle.repository.UserEntityRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JWTUtil jwtUtil;
    private final UserEntityRepository userEntityRepository;

    public Reply saveReply(Long postId, ReplySaveReqDto replySaveReqDto,String authorization){
        Long userId = getUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Reply reply = replySaveReqDto.toEntity(); //내용
        reply.setUser(user); //임의 테스트 //유저저장
        reply.setPost(postRepository.findByPostId(postId));//포스트 저장
        return replyRepository.save(reply);
    }

    //대댓글 저장
    public Reply saveRe_Reply(Long postId , Long parentId, ReplySaveReqDto replySaveReqDto,String authorization){
        Long userId = getUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Reply reply = replySaveReqDto.toEntity();
        reply.setUser(user);
        reply.setPost(postRepository.findByPostId(postId));
        reply.setParent(replyRepository.findById(parentId).get());
        return replyRepository.save(reply);
    }


    public void remove(Long id,String authorization){
        Reply reply = replyRepository.findById(id).get();
        log.info(reply.toString());
        Long userId = getUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        if(!reply.getUser().getUserId().equals(user.getUserId())){
            throw new SecurityException("댓글을 삭제할 권한이 없습니다.");
        }
        reply.removeReply(); // isRemoved = true;
        List<Reply> removeableReplyList = reply.findRemovableList();
        replyRepository.deleteAll(removeableReplyList);
    }
    public Long getUserId(String authorization){
        String token=authorization.replace("Bearer ","");
        String name = jwtUtil.getUsername(token);
        UserEntity userEntity = userEntityRepository.findByName(name);
        User user = userEntity.getUser();
        return user.getUserId();
    }
}
