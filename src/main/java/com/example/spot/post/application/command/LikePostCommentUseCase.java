package com.example.spot.post.application.command;

import com.example.spot.comment.presentation.dto.CommentLikeResponse;

public interface LikePostCommentUseCase {
    //게시글 댓글 좋아요
    CommentLikeResponse likeComment(Long commentId, Long memberId);

    //게시글 댓글 좋아요 취소
    CommentLikeResponse cancelCommentLike(Long commentId, Long memberId);

    //게시글 댓글 싫어요
    CommentLikeResponse dislikeComment(Long commentId, Long memberId);

    //게시글 댓글 싫어요 취소
    CommentLikeResponse cancelCommentDislike(Long commentId, Long memberId);
}
