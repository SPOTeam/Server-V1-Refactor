package com.example.spot.post.infrastructure.querydsl;

import com.example.spot.post.domain.PostComment;
import java.util.List;

public interface PostCommentRepositoryCustom {
    List<PostComment> findCommentsByPostId(Long postId);
}

