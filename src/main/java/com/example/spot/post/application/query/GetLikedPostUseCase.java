package com.example.spot.post.application.query;

public interface GetLikedPostUseCase {

    long countByPostId(Long postId);

    boolean existsByMemberIdAndPostId(Long postId);
}
