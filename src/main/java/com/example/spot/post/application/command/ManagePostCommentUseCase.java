package com.example.spot.post.application.command;

import com.example.spot.post.presentation.dto.request.comment.CommentCreateRequest;
import com.example.spot.post.presentation.dto.response.comment.CommentCreateResponse;

public interface ManagePostCommentUseCase {

    //게시글 댓글 생성
    CommentCreateResponse createComment(Long postId, Long memberId, CommentCreateRequest request);
}
