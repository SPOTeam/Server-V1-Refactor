package com.example.spot.legacy.service.post;

public interface LikedPostCommentQueryService {
    long countByPostCommentIdAndIsLikedTrue(Long postCommentId);

    boolean existsByMemberIdAndPostCommentIdAndIsLikedTrue(Long postCommentId);

    boolean existsByMemberIdAndPostCommentIdAndIsLikedFalse(Long postCommentId);

}
