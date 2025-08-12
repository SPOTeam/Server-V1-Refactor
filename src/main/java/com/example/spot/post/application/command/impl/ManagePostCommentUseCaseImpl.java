package com.example.spot.post.application.command.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.PostHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.post.application.command.ManagePostCommentUseCase;
import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.PostComment;
import com.example.spot.post.infrastructure.jpa.PostCommentRepository;
import com.example.spot.post.infrastructure.jpa.PostRepository;
import com.example.spot.post.infrastructure.jpa.PostStatsRepository;
import com.example.spot.post.presentation.dto.request.comment.CommentCreateRequest;
import com.example.spot.post.presentation.dto.response.comment.CommentCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagePostCommentUseCaseImpl implements ManagePostCommentUseCase {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostStatsRepository postStatsRepository;

    /**
     * 게시글에 댓글을 생성합니다.
     *
     * @param postId   댓글을 작성할 게시글 ID
     * @param memberId 회원 ID
     * @param request  작성할 댓글 정보
     * @return 작성된 댓글 정보와 익명여부 반환
     * @throws PostHandler   게시글을 찾을 수 없는 경우
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     */
    @Transactional
    @Override
    public CommentCreateResponse createComment(Long postId, Long memberId, CommentCreateRequest request) {
        // 게시글과 회원 존재 여부 확인 및 참조 가져오기
        ensurePostAndMemberExist(postId, memberId);
        Post post = requirePostRef(postId);
        Member member = requireMemberRef(memberId);

        // 댓글 생성 및 저장
        PostComment comment = PostComment.builder()
                .content(request.getContent())
                .isAnonymous(request.isAnonymous())
                .post(post)
                .member(member)
                .build();
        PostComment saved = savePostCommentAndIncreaseCommentCount(postId, comment);

        return CommentCreateResponse.toDTO(saved);
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

    private PostComment savePostCommentAndIncreaseCommentCount(Long postId, PostComment comment) {
        postStatsRepository.incrementComment(postId);
        return postCommentRepository.save(comment);
    }
}
