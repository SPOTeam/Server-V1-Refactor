package com.example.spot.post.application.command.impl;

import com.example.spot.comment.domain.PostComment;
import com.example.spot.comment.domain.PostCommentRepository;
import com.example.spot.comment.domain.association.LikedPostComment;
import com.example.spot.comment.domain.association.LikedPostCommentRepository;
import com.example.spot.comment.presentation.dto.CommentLikeResponse;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.PostHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.post.application.command.LikePostCommentUseCase;
import com.example.spot.post.application.query.GetLikedPostCommentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikePostCommentUseCaseImpl implements LikePostCommentUseCase {

    private final MemberRepository memberRepository;
    private final PostCommentRepository postCommentRepository;
    private final LikedPostCommentRepository likedPostCommentRepository;

    private final GetLikedPostCommentUseCase getLikedPostCommentUseCase;

    /**
     * 게시글 댓글에 좋아요를 합니다.
     *
     * @param commentId 좋아요할 댓글 ID
     * @param memberId  회원 ID
     * @return 좋아요한 댓글 ID와 게시글의 현재 좋아요와 싫어요 수
     * @throws PostHandler   댓글을 찾을 수 없는 경우
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   이미 해당 댓글에 좋아요를 한 경우
     */
    @Transactional
    @Override
    public CommentLikeResponse likeComment(Long commentId, Long memberId) {
        // 댓글 조회하기
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_COMMENT_NOT_FOUND));

        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 댓글 좋아요 여부 확인
        if (likedPostCommentRepository.findByMemberIdAndPostCommentIdAndIsLikedTrue(memberId, commentId).isPresent()) {
            throw new PostHandler(ErrorStatus._POST_COMMENT_ALREADY_LIKED);
        }

        // 댓글 좋아요 객체 생성 및 저장 (isLiked가 true면 좋아요, false면 싫어요)
        LikedPostComment likedPostComment = LikedPostComment.builder()
                .postComment(comment)
                .member(member)
                .isLiked(true)
                .build();

        likedPostCommentRepository.saveAndFlush(likedPostComment);

        // 댓글 좋아요 수 조회
        long likeCount = getLikedPostCommentUseCase.countByPostCommentIdAndIsLikedTrue(commentId);

        // 댓글 싫어요 수 조회
        long disLikeCount = likedPostCommentRepository.countByPostCommentIdAndIsLikedFalse(commentId);

        // 댓글 좋아요 결과 반환
        return CommentLikeResponse.toDTO(comment.getId(), likeCount, disLikeCount);
    }

    /**
     * 게시글 댓글에 좋아요를 취소합니다.
     *
     * @param commentId 좋아요 취소할 댓글 ID
     * @param memberId  회원 ID
     * @return 좋아요 취소한 댓글 ID와 게시글의 현재 좋아요와 싫어요 수
     * @throws PostHandler   댓글을 찾을 수 없는 경우
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   좋아요를 하지 않은 댓글일 경우
     */
    @Transactional
    @Override
    public CommentLikeResponse cancelCommentLike(Long commentId, Long memberId) {
        // 댓글 조회하기
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_COMMENT_NOT_FOUND));

        // 회원 정보 가져오기
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 댓글 좋아요 여부 확인
        LikedPostComment likedPostComment = likedPostCommentRepository.findByMemberIdAndPostCommentIdAndIsLikedTrue(
                        memberId, commentId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_COMMENT_NOT_LIKED));

        // 댓글 좋아요 객체 삭제 및 즉시 반영
        likedPostCommentRepository.delete(likedPostComment);
        likedPostCommentRepository.flush();

        // 댓글 좋아요 수 조회
        long likeCount = getLikedPostCommentUseCase.countByPostCommentIdAndIsLikedTrue(commentId);

        // 댓글 싫어요 수 조회
        long disLikeCount = likedPostCommentRepository.countByPostCommentIdAndIsLikedFalse(commentId);

        // 댓글 좋아요 취소 결과 반환
        return CommentLikeResponse.toDTO(comment.getId(), likeCount, disLikeCount);
    }

    /**
     * 게시글 댓글에 싫어요를 합니다.
     *
     * @param commentId 싫어요할 댓글 ID
     * @param memberId  회원 ID
     * @return 싫어요한 댓글 ID와 게시글의 현재 좋아요와 싫어요 수
     * @throws PostHandler   댓글을 찾을 수 없는 경우
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   이미 해당 댓글에 싫어요를 한 경우
     */
    @Transactional
    @Override
    public CommentLikeResponse dislikeComment(Long commentId, Long memberId) {
        // 댓글 조회
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_COMMENT_NOT_FOUND));

        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 싫어요 여부 확인
        if (likedPostCommentRepository.findByMemberIdAndPostCommentIdAndIsLikedFalse(memberId, commentId).isPresent()) {
            throw new PostHandler(ErrorStatus._POST_COMMENT_ALREADY_DISLIKED);
        }

        // 싫어요 객체 생성 및 저장 (isLiked가 true면 좋아요, false면 싫어요)
        LikedPostComment dislikedPostComment = LikedPostComment.builder()
                .postComment(comment)
                .member(member)
                .isLiked(false)
                .build();

        likedPostCommentRepository.saveAndFlush(dislikedPostComment);

        // 댓글 좋아요 수 조회
        long likeCount = getLikedPostCommentUseCase.countByPostCommentIdAndIsLikedTrue(commentId);

        // 댓글 싫어요 수 조회
        long disLikeCount = likedPostCommentRepository.countByPostCommentIdAndIsLikedFalse(commentId);

        // 댓글 싫어요 결과 반환
        return CommentLikeResponse.toDTO(comment.getId(), likeCount, disLikeCount);
    }

    /**
     * 게시글 댓글에 싫어요를 취소합니다.
     *
     * @param commentId 싫어요 취소할 댓글 ID
     * @param memberId  회원 ID
     * @return 싫어요 취소한 댓글 ID와 게시글의 현재 좋아요와 싫어요 수
     * @throws PostHandler   댓글을 찾을 수 없는 경우
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   싫어요를 하지 않은 댓글일 경우
     */
    @Transactional
    @Override
    public CommentLikeResponse cancelCommentDislike(Long commentId, Long memberId) {
        // 댓글 조회
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_COMMENT_NOT_FOUND));

        // 회원 정보 조회
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 싫어요 여부 확인
        LikedPostComment dislikedPostComment = likedPostCommentRepository.findByMemberIdAndPostCommentIdAndIsLikedFalse(
                        memberId, commentId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_COMMENT_NOT_DISLIKED));

        // 싫어요 객체 삭제 및 즉시 반영
        likedPostCommentRepository.delete(dislikedPostComment);
        likedPostCommentRepository.flush();

        // 댓글 좋아요 수 조회
        long likeCount = getLikedPostCommentUseCase.countByPostCommentIdAndIsLikedTrue(commentId);

        // 댓글 싫어요 수 조회
        long disLikeCount = likedPostCommentRepository.countByPostCommentIdAndIsLikedFalse(commentId);

        // 댓글 싫어요 취소 결과 반환
        return CommentLikeResponse.toDTO(comment.getId(), likeCount, disLikeCount);
    }

}
