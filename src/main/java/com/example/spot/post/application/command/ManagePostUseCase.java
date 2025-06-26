package com.example.spot.post.application.command;

import com.example.spot.post.presentation.dto.request.PostCreateRequest;
import com.example.spot.post.presentation.dto.request.PostUpdateRequest;
import com.example.spot.post.presentation.dto.response.PostCreateResponse;

public interface ManagePostUseCase {

    //게시글 생성
    PostCreateResponse createPost(Long memberId, PostCreateRequest postCreateRequest);

    //게시글 삭제
    void deletePost(Long memberId, Long postId);

    //게시글 수정
    PostCreateResponse updatePost(Long memberId, Long postId, PostUpdateRequest postUpdateRequest);
}
