package com.example.spot.story.application.port.in;

import com.example.spot.story.domain.dto.request.StoryCommentRequestDTO;
import com.example.spot.story.domain.dto.request.StoryRequestDTO;
import com.example.spot.story.domain.dto.response.StoryCommentResponseDTO;
import com.example.spot.story.domain.dto.response.StoryResponseDTO;

public interface StoryCommandService {

    // 스터디 게시글 생성
    StoryResponseDTO.StoryPreviewDTO createPost(Long studyId, StoryRequestDTO.StoryDTO postRequestDTO);

    // 스터디 게시글 편집
    StoryResponseDTO.StoryPreviewDTO updatePost(Long studyId, Long postId, StoryRequestDTO.StoryDTO storyDTO);

    // 스터디 게시글 삭제
    StoryResponseDTO.StoryPreviewDTO deletePost(Long studyId, Long postId);

    // 스터디 게시글 좋아요
    StoryResponseDTO.StoryLikeNumDTO likePost(Long studyId, Long postId);

    // 스터디 게시글 좋아요 취소
    StoryResponseDTO.StoryLikeNumDTO cancelPostLike(Long studyId, Long postId);

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
