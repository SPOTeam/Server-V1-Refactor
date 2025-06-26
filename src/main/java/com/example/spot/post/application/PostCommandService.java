package com.example.spot.post.application;

import com.example.spot.comment.presentation.dto.CommentCreateRequest;
import com.example.spot.comment.presentation.dto.CommentCreateResponse;
import com.example.spot.comment.presentation.dto.CommentLikeResponse;
import com.example.spot.post.presentation.dto.request.PostCreateRequest;
import com.example.spot.post.presentation.dto.response.PostCreateResponse;
import com.example.spot.post.presentation.dto.response.PostLikeResponse;
import com.example.spot.post.presentation.dto.request.PostUpdateRequest;
import com.example.spot.post.presentation.dto.request.ScrapAllDeleteRequest;
import com.example.spot.post.presentation.dto.response.ScrapPostResponse;
import com.example.spot.post.presentation.dto.response.ScrapsPostDeleteResponse;

@Deprecated
public interface PostCommandService {

    //게시글 생성
    PostCreateResponse createPost(Long memberId, PostCreateRequest postCreateRequest);

    //게시글 삭제
    void deletePost(Long memberId, Long postId);

    //게시글 수정
    PostCreateResponse updatePost(Long memberId, Long postId, PostUpdateRequest postUpdateRequest);

    //게시글 좋아요
    PostLikeResponse likePost(Long postId, Long memberId);

    //게시글 좋아요 취소
    PostLikeResponse cancelPostLike(Long postId, Long memberId);

    //게시글 댓글 생성
    CommentCreateResponse createComment(Long postId, Long memberId, CommentCreateRequest request);

    //게시글 댓글 좋아요
    CommentLikeResponse likeComment(Long commentId, Long memberId);

    //게시글 댓글 좋아요 취소
    CommentLikeResponse cancelCommentLike(Long commentId, Long memberId);

    //게시글 댓글 싫어요
    CommentLikeResponse dislikeComment(Long commentId, Long memberId);

    //게시글 댓글 싫어요 취소
    CommentLikeResponse cancelCommentDislike(Long commentId, Long memberId);

    //게시글 스크랩
    ScrapPostResponse scrapPost(Long postId, Long memberId);

    //게시글 스크랩 취소
    ScrapPostResponse cancelPostScrap(Long postId, Long memberId);

    //게시글 스크랩 모두 취소
    ScrapsPostDeleteResponse cancelPostScraps(ScrapAllDeleteRequest request);
}
