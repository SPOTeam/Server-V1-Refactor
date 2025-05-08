package com.example.spot.legacy.repository.querydsl;

import com.example.spot.refactor.comment.domain.PostComment;

import java.util.List;

public interface PostCommentRepositoryCustom {
    List<PostComment> findCommentsByPostId(Long postId);
}

