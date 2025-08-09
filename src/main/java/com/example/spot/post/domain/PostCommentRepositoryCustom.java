package com.example.spot.post.domain;

import java.util.List;

public interface PostCommentRepositoryCustom {
    List<PostComment> findCommentsByPostId(Long postId);
}

