package com.example.spot.post.application.query;

public interface GetLikedPostCommentUseCase {
    long countByPostCommentIdAndIsLikedTrue(Long postCommentId);

    boolean existsByMemberIdAndPostCommentIdAndIsLikedTrue(Long postCommentId);

    boolean existsByMemberIdAndPostCommentIdAndIsLikedFalse(Long postCommentId);

}
