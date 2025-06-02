package com.example.spot.story.application;

import com.example.spot.story.domain.enums.StoryCategoryQuery;
import com.example.spot.story.web.dto.response.StoryCommentResponseDTO;
import com.example.spot.story.web.dto.response.StoryResDTO;
import org.springframework.data.domain.PageRequest;

public interface StoryQueryService {

    // 스터디 게시글 목록 불러오기
    StoryResDTO.PostListDTO getAllPosts(PageRequest pageRequest, Long studyId, StoryCategoryQuery storyCategoryQuery);

    // 스터디 게시글 불러오기
    StoryResDTO.PostDetailDTO getPost(Long studyId, Long postId, Boolean likeOrScrap);

    // 스터디 게시글 댓글 목록 불러오기
    StoryCommentResponseDTO.CommentReplyListDTO getAllComments(Long studyId, Long postId);
}
