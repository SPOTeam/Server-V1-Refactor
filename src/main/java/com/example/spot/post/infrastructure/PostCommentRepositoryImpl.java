package com.example.spot.post.infrastructure;


import static com.example.spot.post.domain.QPostComment.postComment;

import com.example.spot.post.domain.PostComment;
import com.example.spot.post.domain.PostCommentRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostCommentRepositoryImpl implements PostCommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PostComment> findCommentsByPostId(Long postId) {
        return jpaQueryFactory
                .selectFrom(postComment)
                .where(
                        postComment.post.id.eq(postId)
                )
                .orderBy(
                        postComment.parentComment.id.asc().nullsFirst(),
                        postComment.createdAt.asc()
                )
                .fetch();
    }
}
