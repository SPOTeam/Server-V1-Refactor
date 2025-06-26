package com.example.spot.post.application.command.impl;

import static com.example.spot.common.security.utils.SecurityUtils.getCurrentUserId;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.PostHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.post.application.command.ScrapPostUseCase;
import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.PostRepository;
import com.example.spot.post.domain.association.MemberScrap;
import com.example.spot.post.domain.association.MemberScrapRepository;
import com.example.spot.post.presentation.dto.request.ScrapAllDeleteRequest;
import com.example.spot.post.presentation.dto.response.ScrapPostResponse;
import com.example.spot.post.presentation.dto.response.ScrapsPostDeleteResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapPostUseCaseImpl implements ScrapPostUseCase {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final MemberScrapRepository memberScrapRepository;

    /**
     * 게시글을 스크랩 합니다.
     * @param postId 스크랩할 게시글 ID
     * @param memberId 회원 ID
     * @return 스크랩한 게시글 ID와 스크랩 수
     * @throws PostHandler 게시글을 찾을 수 없는 경우
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler 이미 해당 게시글을 스크랩 한 경우
     */
    @Override
    public ScrapPostResponse scrapPost(Long postId, Long memberId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_FOUND));

        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 스크랩 여부 확인
        if (memberScrapRepository.findByMemberIdAndPostId(memberId, postId).isPresent()) {
            throw new PostHandler(ErrorStatus._POST_ALREADY_SCRAPPED);
        }

        // 스크랩 정보 저장
        MemberScrap memberScrap = MemberScrap.builder()
                .member(member)
                .post(post)
                .build();

        memberScrapRepository.saveAndFlush(memberScrap);

        // 스크랩된 리스트의 갯수를 조회하여 스크랩 수 계산
        long scrapCount = memberScrapRepository.countByPostId(postId);

        // 스크랩 결과 반환
        return ScrapPostResponse.builder()
                .postId(post.getId())
                .scrapCount(scrapCount)
                .build();
    }

    /**
     * 게시글 스크랩을 취소합니다.
     * @param postId 스크랩 취소할 게시글 ID
     * @param memberId 회원 ID
     * @return 스크랩 취소한 게시글 ID와 스크랩 수
     * @throws PostHandler 게시글을 찾을 수 없는 경우
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler 스크랩하지 않은 게시글인 경우
     */
    @Override
    public ScrapPostResponse cancelPostScrap(Long postId, Long memberId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_FOUND));

        // 회원 정보 가져오기
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 스크랩 여부 확인
        MemberScrap memberScrap = memberScrapRepository.findByMemberIdAndPostId(memberId, postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_SCRAPPED));

        // 스크랩 삭제 및 즉시 반영
        memberScrapRepository.delete(memberScrap);
        memberScrapRepository.flush();

        // 스크랩된 리스트의 갯수를 조회하여 스크랩 수 계산
        long scrapCount = memberScrapRepository.countByPostId(postId);

        // 스크랩 취소 결과 반환
        return ScrapPostResponse.builder()
                .postId(post.getId())
                .scrapCount(scrapCount)
                .build();
    }

    /**
     * 게시글 스크랩 여러개를 한번에 취소합니다.
     * @param request 취소할 스크랩 ID 리스트
     * @return 스크랩 취소 결과 반환
     */
    @Override
    public ScrapsPostDeleteResponse cancelPostScraps(ScrapAllDeleteRequest request) {
        // 현재 로그인한 회원 조회
        Long currentMemberId = getCurrentUserId();

        // 삭제할 List<Long> scrapIds cancelPostScrap() 순회
        List<ScrapPostResponse> deletePostResponses = request.getDeletePostIds().stream().map(
                deletePostId -> cancelPostScrap(deletePostId, currentMemberId)
        ).toList();

        // 스크랩 취소 결과 반환
        return ScrapsPostDeleteResponse.builder()
                .cancelScraps(deletePostResponses)
                .build();
    }
}
