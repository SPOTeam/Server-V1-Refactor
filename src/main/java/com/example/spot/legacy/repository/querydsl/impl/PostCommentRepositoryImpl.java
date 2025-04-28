package com.example.spot.legacy.repository.querydsl.impl;

import com.example.spot.legacy.domain.PostComment;
import com.example.spot.domain.QPostComment;
import com.example.spot.legacy.repository.querydsl.PostCommentRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
