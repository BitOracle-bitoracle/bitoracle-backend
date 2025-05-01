/*
package com.BitOracle.BitOracle.dummy;

import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.domain.Reply;
import com.BitOracle.BitOracle.domain.User;
import com.BitOracle.BitOracle.domain.enums.UserType;
import com.BitOracle.BitOracle.repository.PostRepository;
import com.BitOracle.BitOracle.repository.ReplyRepository;
import com.BitOracle.BitOracle.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.String.valueOf;

@RequiredArgsConstructor
@Component
public class InitService {


    private final Init init;


    @PostConstruct
    public void init(){

        init.save();
    }

    @RequiredArgsConstructor
    @Component
    private static class Init{
        private final UserRepository memberRepository;

        private final PostRepository postRepository;
        private final ReplyRepository commentRepository;

        @Transactional
        public void save() {
            PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();


            //== MEMBER 저장 ==//
            memberRepository.save(User.builder().nickname("밥 잘먹는 동훈이1").userType(UserType.USER).point(0).build());
            memberRepository.save(User.builder().nickname("밥 잘먹는 동훈이2").userType(UserType.USER).point(1).build());
            memberRepository.save(User.builder().nickname("밥 잘먹는 동훈이3").userType(UserType.USER).point(2).build());
            User member = memberRepository.findById(1L).get();


            for(int i = 0; i<=50; i++ ){
                Post post = Post.builder().title(format("게시글 %s", i)).content(format("내용 %s", i)).build();
                post.confirmWriter(memberRepository.findById((long) (i % 3 + 1)).get());
                postRepository.save(post);
            }

            for(int i = 1; i<=150; i++ ){
                Reply comment = Reply.builder().replyContent("댓글" + i).build();
                comment.confirmWriter(memberRepository.findById((long) (i % 3 + 1)).get());
                comment.confirmPost(postRepository.findById(parseLong(valueOf(i%50 + 1))).get());
                commentRepository.save(comment);
            }


            commentRepository.findAll().stream().forEach(comment -> {

                for(int i = 1; i<=50; i++ ){
                    Reply recomment = Reply.builder().replyContent("대댓글" + i).build();
                    recomment.confirmWriter(memberRepository.findById((long) (i % 3 + 1)).get());

                    recomment.confirmPost(comment.getPost());
                    recomment.confirmParent(comment);
                    commentRepository.save(recomment);
                }

            });
        }
    }


}
*/
