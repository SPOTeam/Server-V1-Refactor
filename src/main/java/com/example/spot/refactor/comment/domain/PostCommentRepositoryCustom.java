package com.example.spot.refactor.comment.domain;

import java.util.List;

public interface PostCommentRepositoryCustom {
    List<PostComment> findCommentsByPostId(Long postId);
}

