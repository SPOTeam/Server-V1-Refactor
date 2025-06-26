package com.example.spot.post.presentation.controller.command;

import com.example.spot.comment.presentation.dto.CommentLikeResponse;
import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.post.application.command.LikePostCommentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/spot/posts")
public class LikePostCommentController {

    private final LikePostCommentUseCase likePostCommentUseCase;

    //게시글 댓글 좋아요
    @Tag(name = "게시판 - 댓글", description = "댓글 관련 API")
    @Operation(summary = "[게시판] 댓글 좋아요 API", description = "댓글 ID와 회원 ID를 받아 댓글에 좋아요를 추가합니다.")
    @PostMapping("/comments/{commentId}/like")
    public ApiResponse<CommentLikeResponse> likeComment(
            @PathVariable Long commentId
    ) {
        CommentLikeResponse response = likePostCommentUseCase.likeComment(commentId, SecurityUtils.getCurrentUserId());
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시판 - 댓글", description = "댓글 관련 API")
    @Operation(summary = "[게시판] 댓글 좋아요 취소 API", description = "댓글 ID와 회원 ID를 받아 댓글에 좋아요를 취소합니다.")
    @DeleteMapping("/comments/{commentId}/like")
    public ApiResponse<CommentLikeResponse> cancelCommentLike(
            @PathVariable Long commentId
    ) {
        CommentLikeResponse response = likePostCommentUseCase.cancelCommentLike(commentId, SecurityUtils.getCurrentUserId());
        return ApiResponse.onSuccess(SuccessStatus._NO_CONTENT, response);
    }

    //게시글 댓글 싫어요
    @Tag(name = "게시판 - 댓글", description = "댓글 관련 API")
    @Operation(summary = "[게시판] 댓글 싫어요 API", description = "댓글 ID와 회원 ID를 받아 댓글에 싫어요를 추가합니다.")
    @PostMapping("/comments/{commentId}/dislike")
    public ApiResponse<CommentLikeResponse> dislikeComment(
            @PathVariable Long commentId
    ) {
        CommentLikeResponse response = likePostCommentUseCase.dislikeComment(commentId, SecurityUtils.getCurrentUserId());
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시판 - 댓글", description = "댓글 관련 API")
    @Operation(summary = "[게시판] 댓글 싫어요 취소 API", description = "댓글 ID와 회원 ID를 받아 댓글에 싫어요를 취소합니다.")
    @DeleteMapping("/comments/{commentId}/dislike")
    public ApiResponse<CommentLikeResponse> cancelCommentDislike(
            @PathVariable Long commentId
    ) {
        CommentLikeResponse response = likePostCommentUseCase.cancelCommentDislike(commentId, SecurityUtils.getCurrentUserId());
        return ApiResponse.onSuccess(SuccessStatus._NO_CONTENT, response);
    }

}
