package com.example.spot.post.application;

public interface LikedPostQueryService {
    long countByPostId(Long postId);

    boolean existsByMemberIdAndPostId(Long postId);
}
