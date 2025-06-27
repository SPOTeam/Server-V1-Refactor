package com.example.spot.post.presentation.controller.command;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.post.application.command.LikePostUseCase;
import com.example.spot.post.presentation.dto.response.PostLikeResponse;
import com.example.spot.post.presentation.validator.ExistPost;
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
public class LikePostController {

    private final LikePostUseCase likePostUseCase;

    @Tag(name = "게시글 좋아요", description = "게시글 좋아요 관련 API")
    //게시글 좋아요
    @Operation(summary = "[게시판] 게시글 좋아요 API", description = "게시글 Id를 받아 게시글에 좋아요를 추가합니다.")
    @PostMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> likePost(
            @PathVariable @ExistPost Long postId
    ) {
        PostLikeResponse response = likePostUseCase.likePost(postId, SecurityUtils.getCurrentUserId());
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시글 좋아요", description = "게시글 좋아요 관련 API")
    @Operation(summary = "[게시판] 게시글 좋아요 취소 API", description = "게시글 Id를 받아 게시글에 좋아요를 취소합니다.")
    @DeleteMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> cancelPostLike(
            @PathVariable @ExistPost Long postId
    ) {
        PostLikeResponse response = likePostUseCase.cancelPostLike(postId, SecurityUtils.getCurrentUserId());
        return ApiResponse.onSuccess(SuccessStatus._NO_CONTENT, response);
    }

}
