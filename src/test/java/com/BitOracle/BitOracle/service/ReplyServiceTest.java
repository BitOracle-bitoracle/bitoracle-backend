package com.BitOracle.BitOracle.service;

import com.BitOracle.BitOracle.controller.NewsController;
import com.BitOracle.BitOracle.domain.Reply;
import com.BitOracle.BitOracle.repository.ReplyRepository;
import com.amazonaws.services.s3.AmazonS3;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // application-test.yml 적용
@MockBean(OpenAiService.class) // 또는 NewsService, NewsController 등을 mock 처리
@MockBean(NewsController.class) // 또는 NewsService, NewsController 등을 mock 처리
@MockBean(NewsService.class) // 또는 NewsService, NewsController 등을 mock 처리
@SpringBootTest
@Transactional
class ReplyServiceTest {


    @Autowired
    ReplyService replyService;
    @Autowired
    ReplyRepository replyRepository;
    @Autowired
    EntityManager em;

    @MockBean
    private AmazonS3 amazonS3;


    private void clear(){
        em.flush();
        em.clear();
    }

    private Long saveReply() {
        Reply reply = Reply.builder()
                .replyContent("댓글")
                .build();
        Long id = replyRepository.save(reply).getReplyId();
        clear();
        return id;
    }

    private Long saveReComment(Long parentId){
        Reply parent = replyRepository.findById(parentId).orElse(null);
        Reply comment = Reply.builder().replyContent("댓글").parent(parent).build();

        Long id = replyRepository.save(comment).getReplyId();
        clear();
        return id;
    }
    @Test
    void 댓극ㄹ삭제_대댓글이_남아있는경우() {
        //given
        Long replyId = saveReply();
        saveReComment(replyId);
        saveReComment(replyId);
        saveReComment(replyId);
        assertThat(replyRepository.findById(replyId).get().getChildList().size()).isEqualTo(3);
        //when
        replyService.remove(replyId); //부모삭제
        clear();
        //then
        Reply findReply = replyRepository.findById(replyId).get();
        assertThat(findReply).isNotNull(); //부모 살아잇고
        assertThat(findReply.isRemoved()).isTrue(); //
        assertThat(findReply.getChildList().size()).isEqualTo(3);
    }

    // 댓글을 삭제하는 경우
    //대댓글이 아예 존재하지 않는 경우 : 곧바로 DB에서 삭제
    @Test
    public void 댓글삭제_대댓글이_없는_경우() {
        //given
        Long commentId = saveReply();

        //when
        replyService.remove(commentId);
        clear();

        //then
        Assertions.assertThat(replyRepository.findAll().size()).isSameAs(0);
        //assertThat( assertThrows(Exception.class, () -> replyRepository.findById(commentId)).getMessage()).isEqualTo("댓글이 없습니다.");
    }

    //댓글 삭제
    //대댓글 존재 but 모두 삭제 됨
    //댓글과, 달려잇는 대댓글 모두 db 일괄 삭제, 화면상도 ㄴㄴ
    @Test
    public void 댓글삭제_대댓글존재_but_모두삭제된_대댓글(){
        Long replyId = saveReply();
        Long reComment1Id = saveReComment(replyId);
        Long reComment2Id = saveReComment(replyId);
        Long reComment3Id = saveReComment(replyId);
        Long reComment4Id = saveReComment(replyId);

        assertThat(replyRepository.findById(replyId).get().getChildList().size()).isEqualTo(4);
        clear();
        replyService.remove(reComment1Id);
        clear();
        replyService.remove(reComment2Id);
        clear();
        replyService.remove(reComment3Id);
        clear();
        replyService.remove(reComment4Id);
        clear();
        assertThat(replyRepository.findById(reComment1Id).get().isRemoved()).isTrue();
        assertThat(replyRepository.findById(reComment2Id).get().isRemoved()).isTrue();
        assertThat(replyRepository.findById(reComment3Id).get().isRemoved()).isTrue();
        assertThat(replyRepository.findById(reComment4Id).get().isRemoved()).isTrue();

        //wwhem 댓글 지워쓸때
        replyService.remove(replyId);
        clear();
        //tjem
        assertThat(replyRepository.findById(replyId)).isEmpty();
        assertThat(replyRepository.findById(reComment1Id)).isEmpty();
        assertThat(replyRepository.findById(reComment2Id)).isEmpty();
        assertThat(replyRepository.findById(reComment3Id)).isEmpty();
        assertThat(replyRepository.findById(reComment4Id)).isEmpty();
    }
}