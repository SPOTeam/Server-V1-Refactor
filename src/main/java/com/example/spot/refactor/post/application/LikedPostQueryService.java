package com.example.spot.refactor.post.application;

public interface LikedPostQueryService {
    long countByPostId(Long postId);

    boolean existsByMemberIdAndPostId(Long postId);
}
