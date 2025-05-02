package com.example.spot.legacy.service.studypost;

import com.example.spot.refactor.study.domain.enums.StudyPostCategoryQuery;
import com.example.spot.refactor.study.presentation.dto.response.StudyPostCommentResponseDTO;
import com.example.spot.refactor.study.presentation.dto.response.StudyPostResDTO;
import org.springframework.data.domain.PageRequest;

public interface StudyPostQueryService {

    // 스터디 게시글 목록 불러오기
    StudyPostResDTO.PostListDTO getAllPosts(PageRequest pageRequest, Long studyId, StudyPostCategoryQuery studyPostCategoryQuery);

    // 스터디 게시글 불러오기
    StudyPostResDTO.PostDetailDTO getPost(Long studyId, Long postId, Boolean likeOrScrap);

    // 스터디 게시글 댓글 목록 불러오기
    StudyPostCommentResponseDTO.CommentReplyListDTO getAllComments(Long studyId, Long postId);
}
