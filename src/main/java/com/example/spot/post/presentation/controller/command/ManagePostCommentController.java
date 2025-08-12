package com.example.spot.post.presentation.controller.command;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.post.application.command.ManagePostCommentUseCase;
import com.example.spot.post.presentation.dto.request.comment.CommentCreateRequest;
import com.example.spot.post.presentation.dto.response.comment.CommentCreateResponse;
import com.example.spot.post.presentation.validator.ExistPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/spot/posts")
public class ManagePostCommentController {

    private final ManagePostCommentUseCase managePostCommentUseCase;

    @Tag(name = "게시판 - 댓글", description = "댓글 관련 API")
    @Operation(summary = "[게시판] 댓글 생성 API",
            description = """
                    게시글 Id와 회원 Id를 받아 댓글을 생성합니다.
                    댓글 내용, 익명 여부를 포함한 요청을 받습니다.""")
    @PostMapping("/{postId}/comments")
    public ApiResponse<CommentCreateResponse> createComment(
            @PathVariable @ExistPost Long postId,
            @RequestBody CommentCreateRequest request) {
        CommentCreateResponse response = managePostCommentUseCase.createComment(postId,
                SecurityUtils.getCurrentUserId(), request);
        return ApiResponse.onSuccess(SuccessStatus._CREATED, response);
    }
}
