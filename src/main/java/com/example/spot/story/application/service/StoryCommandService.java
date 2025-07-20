package com.example.spot.story.application.service;

import com.example.spot.story.domain.dto.request.StoryCommentRequestDTO;
import com.example.spot.story.domain.dto.request.StoryRequestDTO;
import com.example.spot.story.domain.dto.response.StoryCommentResponseDTO;
import com.example.spot.story.domain.dto.response.StoryResponseDTO;

public interface StoryCommandService {

    /**
     * 스터디 내부 게시판에 게시글을 작성하는 메서드입니다.
     *
     * @param studyId        게시글을 작성할 타겟 스터디의 아이디를 입력 받습니다.
     * @param postRequestDTO 게시글의 입력 형식(StudyPostRequestDTO.PostDTO)에 맞추어 게시글 정보를 입력 받습니다.
     * @return 작성된 스터디 게시글의 Preview(게시글 아이디, 제목)를 반환합니다.
     */
    StoryResponseDTO.StoryPreviewDTO createPost(Long studyId, StoryRequestDTO.StoryDTO postRequestDTO);


    /**
     * 스터디 내부 게시판에 작성된 게시글을 수정합니다.
     *
     * @param studyId  게시글을 수정할 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId  수정할 스터디 게시글의 아이디를 입력 받습니다.
     * @param storyDTO 게시글의 입력 형식(StudyPostRequestDTO.PostDTO)에 맞추어 게시글 정보를 입력 받습니다.
     * @return 작성된 스터디 게시글의 Preview(게시글 아이디, 제목)를 반환합니다.
     */
    StoryResponseDTO.StoryPreviewDTO updatePost(Long studyId, Long storyId, StoryRequestDTO.StoryDTO storyDTO);


    /**
     * 스터디 내부 게시판에 작성된 게시글을 삭제합니다.
     *
     * @param studyId 게시글을 삭제할 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId  삭제할 스터디 게시글의 아이디를 입력 받습니다.
     * @return 삭제된 스터디 게시글의 Preview(게시글 아이디, 제목)를 반환합니다.
     */
    StoryResponseDTO.StoryPreviewDTO deletePost(Long studyId, Long storyId);


    /**
     * 스터디 내부 게시판에 작성된 게시글에 좋아요를 누르는 메서드입니다. 게시글에 좋아요를 누른 회원의 정보가 StudyLikedPost에 저장되고 스터디 게시글의 좋아요 개수가 업데이트 됩니다.
     *
     * @param studyId 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId  좋아요를 누를 타겟 게시글의 아이디를 입력 받습니다.
     * @return 게시글의 Preview(게시글 아이디, 제목)와 함께 좋아요 개수가 반환됩니다.
     */
    StoryResponseDTO.StoryLikeNumDTO likeStory(Long studyId, Long storyId);


    /**
     * 스터디 내부 게시판에 작성된 게시글에 누른 좋아요를 취소하는 메서드입니다. 게시글에 좋아요를 누른 회원의 정보가 StudyLikedPost에서 삭제되고 스터디 게시글의 좋아요 개수가 업데이트 됩니다.
     *
     * @param studyId 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId  좋아요를 취소할 타겟 게시글의 아이디를 입력 받습니다.
     * @return 게시글의 Preview(게시글 아이디, 제목)와 함께 좋아요 개수가 반환됩니다.
     */
    StoryResponseDTO.StoryLikeNumDTO cancelStoryLike(Long studyId, Long storyId);


    /**
     * 스터디 게시글에 댓글을 추가하는 메서드입니다. 답글 추가 메서드는 하단에 별도로 구현되어 있습니다.
     *
     * @param studyId           스터디 게시글이 작성된 타겟 스터디를 입력 받습니다.
     * @param storyId            댓글을 추가할 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentRequestDTO 추가할 댓글(내용, 익명 여부)을 입력 받습니다.
     * @return 댓글 아이디와 작성자, 내용, 좋아요와 싫어요 개수를 함께 반환합니다.
     */
    StoryCommentResponseDTO.CommentDTO createComment(Long studyId, Long storyId, StoryCommentRequestDTO.CommentDTO commentRequestDTO);


    /**
     * 스터디 게시글에 답글을 추가하는 메서드입니다. 댓글 추가 메서드는 상단에 별도로 구현되어 있습니다.
     *
     * @param studyId           스터디 게시글이 작성된 타겟 스터디를 입력 받습니다.
     * @param storyId            댓글을 추가할 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentRequestDTO 추가할 댓글(내용, 익명 여부)을 입력 받습니다.
     * @return 댓글 아이디와 작성자, 내용, 좋아요와 싫어요 개수를 함께 반환합니다.
     */
    StoryCommentResponseDTO.CommentDTO createReply(Long studyId, Long storyId, Long commentId, StoryCommentRequestDTO.CommentDTO commentRequestDTO);


    /**
     * 스터디 게시글에 작성한 댓글을 삭제하는 메서드입니다. 댓글 삭제와 답글 삭제 모두 해당 메서드를 활용합니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId    댓글을 삭제할 타겟 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 삭제할 댓글의 아이디를 입력 받습니다.
     * @return 삭제한 댓글의 아이디를 반환합니다.
     */
    StoryCommentResponseDTO.CommentIdDTO deleteComment(Long studyId, Long storyId, Long commentId);


    /**
     * 댓글에 좋아요를 누르는 메서드입니다. 댓글 좋아요와 답글 좋아요 모두 해당 메서드를 활용합니다. 댓글에 좋아요를 누른 회원의 정보가 StudyLikedComment에 저장되고 타겟 댓글의 좋아요 개수가
     * 업데이트 됩니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId    타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 좋아요를 누를 타겟 댓글의 아이디를 입력 받습니다.
     * @return 댓글 아이디와 타겟 댓글의 좋아요 수와 싫어요 수가 반환됩니다.
     */
    StoryCommentResponseDTO.CommentPreviewDTO likeComment(Long studyId, Long storyId, Long commentId);


    /**
     * 댓글에 싫어요를 누르는 메서드입니다. 댓글 싫어요와 답글 싫어요 모두 해당 메서드를 활용합니다. 댓글에 싫어요를 누른 회원의 정보가 StudyLikedComment에 저장되고 타겟 댓글의 싫어요 개수가
     * 업데이트 됩니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId    타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 싫어요를 누를 타겟 댓글의 아이디를 입력 받습니다.
     * @return 댓글 아이디와 타겟 댓글의 좋아요 수와 싫어요 수가 반환됩니다.
     */
    StoryCommentResponseDTO.CommentPreviewDTO dislikeComment(Long studyId, Long storyId, Long commentId);


    /**
     * 댓글 좋아요를 취소하는 메서드입니다. 댓글 좋아요와 답글 좋아요 모두 해당 메서드를 활용합니다. 댓글 좋아요를 취소한 회원의 정보가 StudyLikedComment에서 삭제되고 타겟 댓글의 싫어요 개수가
     * 업데이트 됩니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId    타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 싫어요를 취소할 타겟 댓글의 아이디를 입력 받습니다.
     * @return 댓글 아이디와 타겟 댓글의 좋아요 수와 싫어요 수가 반환됩니다.
     */
    StoryCommentResponseDTO.CommentPreviewDTO cancelCommentLike(Long studyId, Long storyId, Long commentId);


    /**
     * 댓글 싫어요를 취소하는 메서드입니다. 댓글 싫어요와 답글 싫어요 모두 해당 메서드를 활용합니다. 댓글 싫어요를 취소한 회원의 정보가 StudyLikedComment에서 삭제되고 타겟 댓글의 싫어요 개수가
     * 업데이트 됩니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId    타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 싫어요를 취소할 타겟 댓글의 아이디를 입력 받습니다.
     * @return 댓글 아이디와 타겟 댓글의 좋아요 수와 싫어요 수가 반환됩니다.
     */
    StoryCommentResponseDTO.CommentPreviewDTO cancelCommentDislike(Long studyId, Long storyId, Long commentId);


}
