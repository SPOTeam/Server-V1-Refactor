package com.example.spot.refactor.post.application;

public interface LikedPostCommentQueryService {
    long countByPostCommentIdAndIsLikedTrue(Long postCommentId);

    boolean existsByMemberIdAndPostCommentIdAndIsLikedTrue(Long postCommentId);

    boolean existsByMemberIdAndPostCommentIdAndIsLikedFalse(Long postCommentId);

}
