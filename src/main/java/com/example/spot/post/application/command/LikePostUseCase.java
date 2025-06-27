package com.example.spot.post.application.command;

import com.example.spot.post.presentation.dto.response.PostLikeResponse;

public interface LikePostUseCase {

    //게시글 좋아요
    PostLikeResponse likePost(Long postId, Long memberId);

    //게시글 좋아요 취소
    PostLikeResponse cancelPostLike(Long postId, Long memberId);
}
