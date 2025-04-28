package com.example.spot.legacy.service.post;

public interface LikedPostQueryService {
    long countByPostId(Long postId);

    boolean existsByMemberIdAndPostId(Long postId);
}
