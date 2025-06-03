package com.example.spot.story.web.controller;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.story.domain.enums.StoryCategoryQuery;
import com.example.spot.story.application.StoryCommandService;
import com.example.spot.story.application.StoryQueryService;
import com.example.spot.story.web.dto.response.StoryResponseDTO;
import com.example.spot.study.domain.validation.annotation.ExistStudy;
import com.example.spot.story.domain.validation.annotation.ExistStory;
import com.example.spot.story.domain.validation.annotation.ExistStoryComment;
import com.example.spot.story.web.dto.request.StoryCommentRequestDTO;
import com.example.spot.story.web.dto.request.StoryRequestDTO;
import com.example.spot.story.web.dto.response.StoryCommentResponseDTO;
import com.example.spot.story.web.dto.response.StoryResDTO;

import com.example.spot.study.presentation.dto.response.StudyImageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spot")
@Validated
public class StoryController {

    private final StoryQueryService storyQueryService;
    private final StoryCommandService storyCommandService;

    /* ----------------------------- 스터디 게시글 관련 API ------------------------------------- */

    @Tag(name = "스터디 게시글")
    @Operation(summary = "[스터디 게시글] 게시글 작성하기", description = """
        ## [스터디 게시글] 내 스터디 > 스터디 > 게시판 > 작성 버튼 클릭, 로그인한 회원이 참여하는 특정 스터디에서 새로운 게시글을 등록합니다.
        스터디에 참여하는 회원이 작성한 게시글을 `study_post`에 저장합니다.
        """)
    @Parameter(name = "studyId", description = "게시글을 작성할 스터디의 id를 입력합니다.", required = true)
    @PostMapping(value = "/studies/{studyId}/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<StoryResDTO.PostPreviewDTO> createPost(
            @PathVariable @ExistStudy Long studyId,
            @ModelAttribute(name = "post") @Valid StoryRequestDTO.PostDTO postRequestDTO) {
        StoryResDTO.PostPreviewDTO postPreviewDTO = storyCommandService.createPost(studyId, postRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_CREATED, postPreviewDTO);
    }

    @Tag(name= "스터디 게시글")
    @Operation(summary = "[스터디 게시글] 게시글 편집하기", description = """
            로그인한 회원이 참여하는 특정 스터디에서 작성한 게시글을 편집합니다.
            수정사항은 study_post db에 반영됩니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "편집할 스터디 게시글의 id를 입력합니다.", required = true)
    @PatchMapping(value = "/studies/{studyId}/posts/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<StoryResDTO.PostPreviewDTO> updatePost(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId,
            @ModelAttribute(name= "post") @Valid StoryRequestDTO.PostDTO postDTO
    ) {
        StoryResDTO.PostPreviewDTO postPreviewDTO = storyCommandService.updatePost(studyId, postId, postDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_UPDATED, postPreviewDTO);
    }

    @Tag(name = "스터디 게시글")
    @Operation(summary = "[스터디 게시글] 게시글 삭제하기", description = """ 
        ## [스터디 게시글] 로그인한 회원이 참여하는 특정 스터디에서 작성한 게시글을 삭제합니다.
        스터디에 참여하는 회원이 작성한 게시글을 study_post에서 삭제합니다.
        게시글에 작성된 댓글도 함께 삭제됩니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "삭제할 스터디 게시글의 id를 입력합니다.", required = true)
    @DeleteMapping("/studies/{studyId}/posts/{postId}")
    public ApiResponse<StoryResDTO.PostPreviewDTO> deletePost(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId) {
        StoryResDTO.PostPreviewDTO postPreviewDTO = storyCommandService.deletePost(studyId, postId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_DELETED, postPreviewDTO);
    }

    @Tag(name = "스터디 게시글")
    @Operation(summary = "[스터디 게시글] 글 목록 불러오기", description = """ 
        ## [스터디 게시글] 내 스터디 > 스터디 > 게시판 클릭, 로그인한 회원이 참여하는 특정 스터디의 게시글 목록을 불러옵니다.
        로그인한 회원이 참여하는 특정 스터디의 study_post 목록이 최신순으로 반환됩니다.
        
        query를 추가하는 경우 해당 카테고리에 속한 스터디 게시글 목록을 반환하며 query가 없는 경우 전체 게시글 목록을 반환합니다.
        
        themeQuery에는 [ANNOUNCEMENT, WELCOME, INFO_SHARING, STUDY_REVIEW, FREE_TALK, QNA] 중 하나를 입력해야 합니다.
        """)
    @Parameter(name = "studyId", description = "게시글 목록을 불러올 스터디의 id를 입력합니다.", required = true)
    @GetMapping("/studies/{studyId}/posts")
    public ApiResponse<StoryResDTO.PostListDTO> getAllPosts(
            @PathVariable @ExistStudy Long studyId,
            @RequestParam(required = false) StoryCategoryQuery storyCategoryQuery,
            @RequestParam @Min(0) Integer offset,
            @RequestParam @Min(1) Integer limit) {
        StoryResDTO.PostListDTO postListDTO = storyQueryService.getAllPosts(PageRequest.of(offset, limit), studyId, storyCategoryQuery);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_LIST_FOUND, postListDTO);
    }

    @Tag(name = "스터디 게시글")
    @Operation(summary = "[스터디 게시글] 게시글 불러오기", description = """ 
        ## [스터디 게시글] 내 스터디 > 스터디 > 게시판 > 게시글 클릭, 로그인한 회원이 참여하는 특정 스터디의 게시글을 불러옵니다.
        로그인한 회원이 참여하는 특정 스터디의 study_post 정보가 반환됩니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "불러올 스터디 게시글의 id를 입력합니다.", required = true)
    @GetMapping("/studies/{studyId}/posts/{postId}")
    public ApiResponse<StoryResDTO.PostDetailDTO> getPost(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId,
            @RequestParam Boolean likeOrScrap) {
        StoryResDTO.PostDetailDTO postDetailDTO = storyQueryService.getPost(studyId, postId, likeOrScrap);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_FOUND, postDetailDTO);
    }

    @Tag(name = "스터디 상세 정보")
    @Operation(summary = "[스터디 상세 정보] 스터디 최근 공지 1개 불러오기", description = """ 
            ## [스터디 상세 정보] 내 스터디 > 스터디 클릭, 로그인한 회원이 참여하는 특정 스터디의 최근 공지 1개를 불러옵니다.
            study_post의 announced_at이 가장 최근인 공지 1개가 반환됩니다.
            """)
    @GetMapping("/studies/{studyId}/announce")
    public ApiResponse<StoryResponseDTO> getRecentAnnouncement(@PathVariable @ExistStudy Long studyId) {
        StoryResponseDTO storyResponseDTO = storyQueryService.findStudyAnnouncementPost(studyId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_FOUND, storyResponseDTO);
    }

    @Tag(name = "스터디 게시글")
    @Operation(summary = "[스터디 게시글] 좋아요 누르기", description = """ 
        ## [스터디 게시글] 내 스터디 > 스터디 > 게시판 > 게시글 클릭, 로그인한 회원이 참여하는 특정 스터디의 게시글에 좋아요를 누릅니다.
        study_liked_post에 좋아요를 누른 회원의 정보를 저장하고 게시글의 like_num을 업데이트합니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "좋아요를 누를 스터디 게시글의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/posts/{postId}/likes")
    public ApiResponse<StoryResDTO.PostLikeNumDTO> likePost(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId) {
        StoryResDTO.PostLikeNumDTO postLikeNumDTO = storyCommandService.likePost(studyId, postId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_LIKED, postLikeNumDTO);
    }

    @Tag(name = "스터디 게시글")
    @Operation(summary = "[스터디 게시글] 좋아요 취소하기", description = """ 
        ## [스터디 게시글] 내 스터디 > 스터디 > 게시판 > 게시글 클릭, 로그인한 회원이 참여하는 특정 스터디의 게시글에 좋아요를 취소합니다.
        study_liked_post에 좋아요를 누른 회원의 정보를 저장하고 게시글의 like_num을 업데이트합니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "좋아요를 취소할 스터디 게시글의 id를 입력합니다.", required = true)
    @DeleteMapping("/studies/{studyId}/posts/{postId}/likes")
    public ApiResponse<StoryResDTO.PostLikeNumDTO> cancelPostLike(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId) {
        StoryResDTO.PostLikeNumDTO postLikeNumDTO = storyCommandService.cancelPostLike(studyId, postId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_DISLIKED, postLikeNumDTO);
    }

/* ----------------------------- 스터디 게시글 댓글 관련 API ------------------------------------- */

    @Tag(name = "스터디 게시글 - 댓글")
    @Operation(summary = "[스터디 게시글 - 댓글] 댓글 작성하기", description = """ 
        ## [스터디 게시글] 로그인한 회원이 참여하는 특정 스터디의 게시글에 댓글을 작성합니다.
        RequestBody로 내용과 회원 정보를 입력 받아 댓글 정보를 반환합니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "댓글을 작성할 스터디 게시글의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/posts/{postId}/comments")
    public ApiResponse<StoryCommentResponseDTO.CommentDTO> createComment(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId,
            @RequestBody @Valid StoryCommentRequestDTO.CommentDTO commentRequestDTO) {
        StoryCommentResponseDTO.CommentDTO commentResponseDTO = storyCommandService.createComment(studyId, postId, commentRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_COMMENT_CREATED, commentResponseDTO);
    }

    @Tag(name = "스터디 게시글 - 댓글")
    @Operation(summary = "[스터디 게시글 - 댓글] 답글 작성하기", description = """ 
        ## [스터디 게시글] 로그인한 회원이 참여하는 특정 스터디 게시글의 댓글에 대하여 답글을 작성합니다.
        RequestBody로 내용과 회원 정보를 입력 받아 답글 정보를 반환합니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "스터디 게시글의 id를 입력합니다.", required = true)
    @Parameter(name = "commentId", description = "답글을 작성할 댓글의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/posts/{postId}/comments/{commentId}/replies")
    public ApiResponse<StoryCommentResponseDTO.CommentDTO> createReply(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId,
            @PathVariable @ExistStoryComment Long commentId,
            @RequestBody @Valid StoryCommentRequestDTO.CommentDTO commentRequestDTO) {
        StoryCommentResponseDTO.CommentDTO commentResponseDTO = storyCommandService.createReply(studyId, postId, commentId, commentRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_COMMENT_CREATED, commentResponseDTO);
    }

    @Tag(name = "스터디 게시글 - 댓글")
    @Operation(summary = "[스터디 게시글 - 댓글] 댓글 삭제하기", description = """ 
        ## [스터디 게시글] 로그인한 회원이 참여하는 특정 스터디 게시글의 댓글을 삭제합니다.
        댓글의 id를 PathVariable로 받아 content와 isDeleted를 수정합니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "스터디 게시글의 id를 입력합니다.", required = true)
    @Parameter(name = "commentId", description = "삭제할 댓글의 id를 입력합니다.", required = true)
    @PatchMapping("/studies/{studyId}/posts/{postId}/comments/{commentId}")
    public ApiResponse<StoryCommentResponseDTO.CommentIdDTO> deleteComment(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId,
            @PathVariable @ExistStoryComment Long commentId) {
        StoryCommentResponseDTO.CommentIdDTO commentPreviewDTO = storyCommandService.deleteComment(studyId, postId, commentId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_COMMENT_DELETED, commentPreviewDTO);
    }

    @Tag(name = "스터디 게시글 - 댓글")
    @Operation(summary = "[스터디 게시글 - 댓글] 댓글 좋아요 누르기", description = """ 
        ## [스터디 게시글] 로그인한 회원이 참여하는 특정 스터디 게시글의 댓글에 좋아요를 누릅니다.
        study_liked_comment에 좋아요 내역이 추가되고 study_post_comment의 like_count가 증가합니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "스터디 게시글의 id를 입력합니다.", required = true)
    @Parameter(name = "commentId", description = "좋아요를 누를 댓글의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/posts/{postId}/comments/{commentId}/likes")
    public ApiResponse<StoryCommentResponseDTO.CommentPreviewDTO> likeComment(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId,
            @PathVariable @ExistStoryComment Long commentId) {
        StoryCommentResponseDTO.CommentPreviewDTO commentPreviewDTO = storyCommandService.likeComment(studyId, postId, commentId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_COMMENT_LIKED, commentPreviewDTO);
    }

    @Tag(name = "스터디 게시글 - 댓글")
    @Operation(summary = "[스터디 게시글 - 댓글] 댓글 싫어요 누르기", description = """ 
        ## [스터디 게시글] 로그인한 회원이 참여하는 특정 스터디 게시글의 댓글에 싫어요를 누릅니다.
        study_liked_comment에 싫어요 내역이 추가되고 study_post_comment의 dislike_count가 증가합니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "스터디 게시글의 id를 입력합니다.", required = true)
    @Parameter(name = "commentId", description = "싫어요를 누를 댓글의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/posts/{postId}/comments/{commentId}/dislikes")
    public ApiResponse<StoryCommentResponseDTO.CommentPreviewDTO> dislikeComment(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId,
            @PathVariable @ExistStoryComment Long commentId) {
        StoryCommentResponseDTO.CommentPreviewDTO commentPreviewDTO = storyCommandService.dislikeComment(studyId, postId, commentId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_COMMENT_DISLIKED, commentPreviewDTO);
    }

    @Tag(name = "스터디 게시글 - 댓글")
    @Operation(summary = "[스터디 게시글 - 댓글] 댓글 좋아요 취소하기", description = """ 
        ## [스터디 게시글] 로그인한 회원이 참여하는 특정 스터디 게시글 댓글에 달린 좋아요를 취소합니다.
        study_liked_comment에서 좋아요 내역이 삭제되고 study_post_comment의 like_count가 감소합니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "스터디 게시글의 id를 입력합니다.", required = true)
    @Parameter(name = "commentId", description = "좋아요를 취소할 댓글의 id를 입력합니다.", required = true)
    @DeleteMapping("/studies/{studyId}/posts/{postId}/comments/{commentId}/likes")
    public ApiResponse<StoryCommentResponseDTO.CommentPreviewDTO> cancelCommentLike(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId,
            @PathVariable @ExistStoryComment Long commentId) {
        StoryCommentResponseDTO.CommentPreviewDTO commentPreviewDTO = storyCommandService.cancelCommentLike(studyId, postId, commentId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_COMMENT_LIKE_CANCELED, commentPreviewDTO);
    }

    @Tag(name = "스터디 게시글 - 댓글")
    @Operation(summary = "[스터디 게시글 - 댓글] 댓글 싫어요 취소하기", description = """ 
        ## [스터디 게시글] 로그인한 회원이 참여하는 특정 스터디 게시글 댓글에 달린 싫어요를 취소합니다.
        study_liked_comment에서 싫어요 내역이 삭제되고 study_post_comment의 dislike_count가 감소합니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "스터디 게시글의 id를 입력합니다.", required = true)
    @Parameter(name = "commentId", description = "싫어요를 취소할 댓글의 id를 입력합니다.", required = true)
    @DeleteMapping("/studies/{studyId}/posts/{postId}/comments/{commentId}/dislikes")
    public ApiResponse<StoryCommentResponseDTO.CommentPreviewDTO> cancelCommentDislike(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId,
            @PathVariable @ExistStoryComment Long commentId) {
        StoryCommentResponseDTO.CommentPreviewDTO commentPreviewDTO = storyCommandService.cancelCommentDislike(studyId, postId, commentId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_COMMENT_DISLIKE_CANCELED, commentPreviewDTO);
    }

    @Tag(name = "스터디 게시글 - 댓글")
    @Operation(summary = "[스터디 게시글 - 댓글] 전체 댓글 불러오기", description = """ 
        ## [스터디 게시글] 내 스터디 > 스터디 > 게시판 > 게시글 클릭, 로그인한 회원이 참여하는 특정 스터디의 게시글에 달린 모든 댓글을 불러옵니다.
        특정 study_post에 대한 comment(댓/답글) 목록이 반환됩니다.
        """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "댓글을 불러올 스터디 게시글의 id를 입력합니다.", required = true)
    @GetMapping("/studies/{studyId}/posts/{postId}/comments")
    public ApiResponse<StoryCommentResponseDTO.CommentReplyListDTO> getAllComments(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId) {
        StoryCommentResponseDTO.CommentReplyListDTO commentReplyListDTO = storyQueryService.getAllComments(studyId, postId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_COMMENT_FOUND, commentReplyListDTO);
    }

/* ----------------------------- 스터디 게시글 이미지 관련 API ------------------------------------- */

    @Tag(name = "스터디 게시글 - 갤러리")
    @Operation(summary = "[스터디 게시글 - 갤러리] 스터디 이미지 목록 불러오기", description = """ 
            ## [스터디 게시글] 내 스터디 > 스터디 > 갤러리 클릭, 로그인한 회원이 참여하는 스터디의 이미지 목록을 불러옵니다.
            스터디에 존재하는 모든 게시글의 이미지를 최신순으로 반환합니다.
            """)
    @Parameter(name = "studyId", description = "이미지 목록을 불러올 스터디의 id를 입력합니다.", required = true)
    @GetMapping("/studies/{studyId}/images")
    public ApiResponse<StudyImageResponseDTO.ImageListDTO> getAllStudyImages(
            @PathVariable @ExistStudy Long studyId,
            @RequestParam @Min(0) Integer offset,
            @RequestParam @Min(1) Integer limit) {
        StudyImageResponseDTO.ImageListDTO imageListDTO = storyQueryService.getAllStudyImages(studyId, PageRequest.of(offset, limit));
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_IMAGES_FOUND, imageListDTO);
    }

}
