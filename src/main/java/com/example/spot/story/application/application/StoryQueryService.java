package com.example.spot.story.application.application;

import com.example.spot.story.domain.enums.StoryCategoryQuery;
import com.example.spot.study.presentation.dto.response.StudyPostCommentResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyPostResDTO;
import org.springframework.data.domain.PageRequest;

public interface StoryQueryService {

    // 스터디 게시글 목록 불러오기
    StudyPostResDTO.PostListDTO getAllPosts(PageRequest pageRequest, Long studyId, StoryCategoryQuery storyCategoryQuery);

    // 스터디 게시글 불러오기
    StudyPostResDTO.PostDetailDTO getPost(Long studyId, Long postId, Boolean likeOrScrap);

    // 스터디 게시글 댓글 목록 불러오기
    StudyPostCommentResponseDTO.CommentReplyListDTO getAllComments(Long studyId, Long postId);
}
