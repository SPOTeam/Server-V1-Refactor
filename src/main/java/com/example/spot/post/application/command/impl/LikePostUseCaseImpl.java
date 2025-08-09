package com.example.spot.post.application.command.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.PostHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.post.application.command.LikePostUseCase;
import com.example.spot.post.application.query.GetLikedPostUseCase;
import com.example.spot.post.domain.Post;
import com.example.spot.post.infrastructure.jpa.PostRepository;
import com.example.spot.post.domain.association.LikedPost;
import com.example.spot.post.infrastructure.jpa.LikedPostRepository;
import com.example.spot.post.presentation.dto.response.post.PostLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikePostUseCaseImpl implements LikePostUseCase {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final LikedPostRepository likedPostRepository;

    private final GetLikedPostUseCase getLikedPostUseCase;

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
        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_FOUND));
        //좋아요 여부 확인
        if (likedPostRepository.findByMemberIdAndPostId(memberId, postId).isPresent()) {
            throw new PostHandler(ErrorStatus._POST_ALREADY_LIKED);
        }

        // 좋아요 객체 생성 및 저장
        LikedPost likedPost = LikedPost.builder()
                .post(post)
                .member(member)
                .build();

        likedPostRepository.saveAndFlush(likedPost);

        // 게시글의 현재 좋아요 수 조회
        long likeCount = getLikedPostUseCase.countByPostId(postId);

        // 좋아요 결과 반환
        return PostLikeResponse.builder()
                .postId(post.getId())
                .likeCount(likeCount)
                .build();
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
        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_FOUND));
        // 좋아요 여부 확인
        LikedPost likedPost = likedPostRepository.findByMemberIdAndPostId(member.getId(), post.getId())
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_LIKED));

        // 좋아요 객체 삭제
        likedPostRepository.delete(likedPost);
        likedPostRepository.flush();

        // 게시글의 현재 좋아요 수 조회
        long likeCount = getLikedPostUseCase.countByPostId(postId);

        // 좋아요 취소 결과 반환
        return PostLikeResponse.builder()
                .postId(post.getId())
                .likeCount(likeCount)
                .build();
    }
}
