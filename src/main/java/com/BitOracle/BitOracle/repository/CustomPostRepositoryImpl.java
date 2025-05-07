package com.BitOracle.BitOracle.repository;
import static com.BitOracle.BitOracle.domain.QPost.post;
import static com.BitOracle.BitOracle.domain.QUser.user;

import com.BitOracle.BitOracle.domain.Post;
import com.BitOracle.BitOracle.dummy.PostSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
//제목 or 작성자 검색
@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory query;
    public CustomPostRepositoryImpl(EntityManager em) {
        query = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> search(PostSearchCondition postSearchCondition, Pageable pageable) {
        BooleanExpression filter = combineOr(postSearchCondition);

        List<Post> content = query.selectFrom(post)
                .where(
                        filter
                )
                .leftJoin(post.user, user)
                .fetchJoin()
                .orderBy(post.createdAt.desc()) //최신순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(); //Count쿼리 발생 x

        JPAQuery<Post> countQuery = query.selectFrom(post)
                .where(
                        filter
                );
        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }
    private BooleanExpression contentHasStr(String content) {
        return StringUtils.hasLength(content) ? post.content.contains(content) : null;
    }
    private BooleanExpression titleHasStr(String title) {
        return StringUtils.hasLength(title) ? post.title.contains(title) : null;
    }

    private BooleanExpression combineOr(PostSearchCondition condition) {
        BooleanExpression titleExpr = titleHasStr(condition.getTitle());
        BooleanExpression contentExpr = contentHasStr(condition.getContent());

        if (titleExpr != null && contentExpr != null) {
            return titleExpr.or(contentExpr);
        } else if (titleExpr != null) {
            return titleExpr;
        } else if (contentExpr != null) {
            return contentExpr;
        } else {
            return null;
        }
    }
}
