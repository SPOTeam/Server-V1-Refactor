package com.example.spot.post.application;

@Deprecated
public interface LikedPostQueryService {
    long countByPostId(Long postId);

    boolean existsByMemberIdAndPostId(Long postId);
}
