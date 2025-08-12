package com.example.spot.post.application.command.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.PostHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.post.application.command.LikePostUseCase;
import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.association.LikedPost;
import com.example.spot.post.infrastructure.jpa.LikedPostRepository;
import com.example.spot.post.infrastructure.jpa.PostRepository;
import com.example.spot.post.infrastructure.jpa.PostStatsRepository;
import com.example.spot.post.presentation.dto.response.post.PostLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikePostUseCaseImpl implements LikePostUseCase {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final LikedPostRepository likedPostRepository;
    private final PostStatsRepository postStatsRepository;

    /**
     * 게시글에 좋아요를 합니다.
     *
     * @param postId   좋아요할 게시글 ID
     * @param memberId 회원 ID
     * @return 좋아요한 게시글 ID와 게시글의 현재 좋아요 수
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   게시글을 찾을 수 없는 경우
     * @throws PostHandler   이미 해당 게시글에 좋아요를 누른 경우
     */
    @Transactional
    @Override
    public PostLikeResponse likePost(Long postId, Long memberId) {
        checkIsExistPostAndMember(postId, memberId);

        // 회원 정보 가져오기
        Member memberRef = getMemberRef(memberId);
        Post postRef = getPostRef(postId);

        saveLikePostAndIncreaseLikeCount(postId, postRef, memberRef);

        return getPostLikeResponse(postId, postRef);
    }

    /**
     * 게시글 좋아요를 취소합니다.
     *
     * @param postId   좋아요를 취소할 게시글 ID
     * @param memberId 회원 ID
     * @return 좋아요를 취소한 게시글 ID와 게시글의 현재 좋아요 수
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   게시글을 찾을 수 없는 경우
     * @throws PostHandler   좋아요 하지 않은 게시글일 경우
     */
    @Transactional
    @Override
    public PostLikeResponse cancelPostLike(Long postId, Long memberId) {
        checkIsExistPostAndMember(postId, memberId);
        Post postRef = getPostRef(postId);

        deleteLikePostAndDecreaseLikeCount(postId, memberId);

        // 게시글의 현재 좋아요 수 조회
        return getPostLikeResponse(postId, postRef);
    }

    /**
     * ------------------------------- private method ------------------------------------------
     **/

    private Member getMemberRef(Long memberId) {
        return memberRepository.getReferenceById(memberId);
    }

    private Post getPostRef(Long postId) {
        return postRepository.getReferenceById(postId);
    }

    private void checkIsExistPostAndMember(Long postId, Long memberId) {
        // 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw new PostHandler(ErrorStatus._POST_NOT_FOUND);
        }

        // 회원 존재 여부 확인
        if (!memberRepository.existsById(memberId)) {
            throw new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND);
        }
    }

    private void saveLikePostAndIncreaseLikeCount(Long postId, Post postRef, Member memberRef) {
        try {
            // 1) 좋아요 행 삽입 시도
            likedPostRepository.save(
                    LikedPost.builder()
                            .post(postRef)
                            .member(memberRef)
                            .build()
            );

            // 2) 삽입 성공한 경우에만 카운터 +1
            postStatsRepository.incrementLike(postId);
        } catch (DataIntegrityViolationException e) {
            throw new PostHandler(ErrorStatus._POST_ALREADY_LIKED);
        }
    }

    private void deleteLikePostAndDecreaseLikeCount(Long postId, Long memberId) {
        int effectedRow = likedPostRepository.deleteByMemberIdAndPostId(memberId, postId);
        if (effectedRow == 1) {
            postStatsRepository.decrementLike(postId);
        }
    }

    private PostLikeResponse getPostLikeResponse(Long postId, Post postRef) {
        long likeCount = postStatsRepository.getLikeCount(postId);
        // 좋아요 결과 반환
        return PostLikeResponse.builder()
                .postId(postRef.getId())
                .likeCount(likeCount)
                .build();
    }
}
