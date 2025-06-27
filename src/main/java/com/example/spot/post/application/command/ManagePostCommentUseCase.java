package com.example.spot.post.application.command;

import com.example.spot.comment.presentation.dto.CommentCreateRequest;
import com.example.spot.comment.presentation.dto.CommentCreateResponse;

public interface ManagePostCommentUseCase {

    //게시글 댓글 생성
    CommentCreateResponse createComment(Long postId, Long memberId, CommentCreateRequest request);
}
