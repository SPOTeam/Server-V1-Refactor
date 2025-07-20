package com.example.spot.story.application.service;

import com.example.spot.story.domain.enums.StoryCategoryQuery;
import com.example.spot.story.domain.dto.response.StoryCommentResponseDTO;
import com.example.spot.story.domain.dto.response.StoryResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyImageResponseDTO;
import org.springframework.data.domain.PageRequest;

public interface StoryQueryService {

    // 스터디 게시글 목록 불러오기
    StoryResponseDTO.StoryListDTO getAllPosts(PageRequest pageRequest, Long studyId, StoryCategoryQuery storyCategoryQuery);

    // 스터디 게시글 불러오기
    StoryResponseDTO.StoryDetailDTO getPost(Long studyId, Long postId, Boolean likeOrScrap);

    // 스터디 공지 게시글 불러오기
    StoryResponseDTO.StoryContentDTO findStudyAnnouncementPost(Long studyId);

    // 스터디 게시글 댓글 목록 불러오기
    StoryCommentResponseDTO.ReplyListDTO getAllComments(Long studyId, Long postId);

    // 스터디 이미지 목록 조회
    StudyImageResponseDTO.ImageListDTO getAllStudyImages(Long studyId, PageRequest pageRequest);

}
