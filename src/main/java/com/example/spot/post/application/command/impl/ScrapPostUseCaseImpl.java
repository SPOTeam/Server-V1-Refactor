package com.example.spot.post.application.command.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.PostHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.post.application.command.ScrapPostUseCase;
import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.association.MemberScrap;
import com.example.spot.post.infrastructure.jpa.MemberScrapRepository;
import com.example.spot.post.infrastructure.jpa.PostRepository;
import com.example.spot.post.infrastructure.jpa.PostStatsRepository;
import com.example.spot.post.presentation.dto.request.post.ScrapAllDeleteRequest;
import com.example.spot.post.presentation.dto.response.post.ScrapPostResponse;
import com.example.spot.post.presentation.dto.response.post.ScrapsPostDeleteResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapPostUseCaseImpl implements ScrapPostUseCase {

    private final PostRepository postRepository;
    private final PostStatsRepository postStatsRepository;
    private final MemberRepository memberRepository;
    private final MemberScrapRepository memberScrapRepository;

    /**
     * 게시글을 스크랩 합니다.
     *
     * @param postId   스크랩할 게시글 ID
     * @param memberId 회원 ID
     * @return 스크랩한 게시글 ID와 스크랩 수
     * @throws PostHandler   게시글을 찾을 수 없는 경우
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   이미 해당 게시글을 스크랩 한 경우
     */
    @Override
    public ScrapPostResponse scrapPost(Long postId, Long memberId) {
        ensurePostAndMemberExist(postId, memberId);

        Post post = requirePostRef(postId);
        Member member = requireMemberRef(memberId);

        saveMemberScrapAndIncreaseScrapNum(postId, member, post);

        // 스크랩된 리스트의 갯수를 조회하여 스크랩 수 계산
        return getScrapPostResponse(postId, post);
    }

    /**
     * 게시글 스크랩을 취소합니다.
     *
     * @param postId   스크랩 취소할 게시글 ID
     * @param memberId 회원 ID
     * @return 스크랩 취소한 게시글 ID와 스크랩 수
     * @throws PostHandler   게시글을 찾을 수 없는 경우
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   스크랩하지 않은 게시글인 경우
     */
    @Override
    public ScrapPostResponse cancelPostScrap(Long postId, Long memberId) {
        ensurePostAndMemberExist(postId, memberId);

        Post post = requirePostRef(postId);

        deleteMemberScrapAndDecreaseScrapNum(postId, memberId);

        return getScrapPostResponse(postId, post);
    }

    /**
     * 게시글 스크랩 여러개를 한번에 취소합니다.
     *
     * @param request 취소할 스크랩 ID 리스트
     * @return 스크랩 취소 결과 반환
     */
    @Override
    public ScrapsPostDeleteResponse cancelPostScraps(ScrapAllDeleteRequest request, Long memberId) {
        // 삭제할 List<Long> scrapIds cancelPostScrap() 순회
        List<ScrapPostResponse> deletePostResponses = request.getDeletePostIds().stream()
                .map(deletePostId -> cancelPostScrap(deletePostId, memberId))
                .toList();

        // 스크랩 취소 결과 반환
        return ScrapsPostDeleteResponse.builder()
                .cancelScraps(deletePostResponses)
                .build();
    }


    /* ------------------------------- private method ------------------------------------------ */

    private void ensurePostAndMemberExist(Long postId, Long memberId) {
        // 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw new PostHandler(ErrorStatus._POST_NOT_FOUND);
        }

        // 회원 존재 여부 확인
        if (!memberRepository.existsById(memberId)) {
            throw new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND);
        }
    }

    private Member requireMemberRef(Long memberId) {
        return memberRepository.getReferenceById(memberId);
    }

    private Post requirePostRef(Long postId) {
        return postRepository.getReferenceById(postId);
    }

    private void saveMemberScrapAndIncreaseScrapNum(Long postId, Member member, Post post) {
        // 스크랩 정보 저장
        MemberScrap memberScrap = MemberScrap.builder()
                .member(member)
                .post(post)
                .build();

        memberScrapRepository.save(memberScrap);
        postStatsRepository.incrementScrap(postId);
    }

    private void deleteMemberScrapAndDecreaseScrapNum(Long postId, Long memberId) {
        // 스크랩 삭제 및 즉시 반영
        int effectedRow = memberScrapRepository.deleteByPostIdAndMemberId(postId, memberId);
        if (effectedRow == 1) {
            postStatsRepository.decrementScrap(postId);
        }
    }

    private ScrapPostResponse getScrapPostResponse(Long postId, Post post) {
        // 스크랩된 리스트의 갯수를 조회하여 스크랩 수 계산
        long scrapCount = postStatsRepository.getScrapNum(postId);
        return ScrapPostResponse.builder()
                .postId(post.getId())
                .scrapCount(scrapCount)
                .build();
    }
}
