package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.domain.Reply;
import com.BitOracle.BitOracle.dto.ReplySaveReqDto;
import com.BitOracle.BitOracle.repository.PostRepository;
import com.BitOracle.BitOracle.repository.ReplyRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
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

    public Reply saveReply(Long postId, ReplySaveReqDto replySaveReqDto){
        Reply reply = replySaveReqDto.toEntity(); //내용
        reply.setUser(userRepository.findByNickname("admin")); //임의 테스트 //유저저장
        reply.setPost(postRepository.findByPostId(postId));//포스트 저장
        return replyRepository.save(reply);
    }

    //대댓글 저장
    public void saveRe_Reply(Long postId , Long parentId, ReplySaveReqDto replySaveReqDto){
        Reply reply = replySaveReqDto.toEntity();
        reply.setUser(userRepository.findByNickname("admin"));
        reply.setPost(postRepository.findByPostId(postId));
        reply.setParent(replyRepository.findById(parentId).get());
        replyRepository.save(reply);
    }

/*    //지울수잇는 댓글 지우기
    public void removeReply(Long id) throws Exception{
        Reply reply = replyRepository.findById(id).orElseThrow(()->new Exception("해당 유저의 댓글이 없습니다"));
        log.info(reply.toString());
        List<Reply> removableReplyList = reply.findRemovableList();//제거 가능 댓글 배열
        removableReplyList.forEach(removableReply -> replyRepository.delete(removableReply));//리스트 돌면서 댓글 삭제
    }*/

    public void remove(Long id){
        Reply reply = replyRepository.findById(id).get();
        log.info(reply.toString());
        reply.removeReply(); // isRemoved = true;
        List<Reply> removeableReplyList = reply.findRemovableList();
        replyRepository.deleteAll(removeableReplyList);
    }
}
