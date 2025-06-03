package com.example.spot.story.application;

import com.example.spot.story.web.dto.request.StoryCommentRequestDTO;
import com.example.spot.story.web.dto.request.StoryRequestDTO;
import com.example.spot.story.web.dto.response.StoryCommentResponseDTO;
import com.example.spot.story.web.dto.response.StoryResDTO;

public interface StoryCommandService {

    // 스터디 게시글 생성
    StoryResDTO.PostPreviewDTO createPost(Long studyId, StoryRequestDTO.PostDTO postRequestDTO);

    // 스터디 게시글 편집
    StoryResDTO.PostPreviewDTO updatePost(Long studyId, Long postId, StoryRequestDTO.PostDTO postDTO);

    // 스터디 게시글 삭제
    StoryResDTO.PostPreviewDTO deletePost(Long studyId, Long postId);

    // 스터디 게시글 좋아요
    StoryResDTO.PostLikeNumDTO likePost(Long studyId, Long postId);

    // 스터디 게시글 좋아요 취소
    StoryResDTO.PostLikeNumDTO cancelPostLike(Long studyId, Long postId);

    // 스터디 게시글 댓글 생성
    StoryCommentResponseDTO.CommentDTO createComment(Long studyId, Long postId, StoryCommentRequestDTO.CommentDTO commentRequestDTO);

    // 스터디 게시글 답글 생성
    StoryCommentResponseDTO.CommentDTO createReply(Long studyId, Long postId, Long commentId, StoryCommentRequestDTO.CommentDTO commentRequestDTO);

    // 스터디 게시글 댓글 삭제 (댓/답글 구분 X)
    StoryCommentResponseDTO.CommentIdDTO deleteComment(Long studyId, Long postId, Long commentId);

    // 스터디 게시글 댓글 좋아요
    StoryCommentResponseDTO.CommentPreviewDTO likeComment(Long studyId, Long postId, Long commentId);

    // 스터디 게시글 댓글 싫어요
    StoryCommentResponseDTO.CommentPreviewDTO dislikeComment(Long studyId, Long postId, Long commentId);

    // 스터디 게시글 댓글 좋아요 취소
    StoryCommentResponseDTO.CommentPreviewDTO cancelCommentLike(Long studyId, Long postId, Long commentId);

    // 스터디 게시글 댓글 싫어요 취소
    StoryCommentResponseDTO.CommentPreviewDTO cancelCommentDislike(Long studyId, Long postId, Long commentId);

}
