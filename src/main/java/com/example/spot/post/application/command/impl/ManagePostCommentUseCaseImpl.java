package com.example.spot.post.application.command.impl;

import com.example.spot.post.domain.PostComment;
import com.example.spot.post.infrastructure.jpa.PostCommentRepository;
import com.example.spot.post.presentation.dto.request.comment.CommentCreateRequest;
import com.example.spot.post.presentation.dto.response.comment.CommentCreateResponse;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.PostHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.post.application.command.ManagePostCommentUseCase;
import com.example.spot.post.domain.Post;
import com.example.spot.post.infrastructure.jpa.PostRepository;
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
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_FOUND));

        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 부모 댓글 생성
        PostComment comment = PostComment.builder()
                .content(request.getContent())
                .isAnonymous(request.isAnonymous())
                .post(post)
                .parentComment(null)
                .member(member)
                .build();

        // 댓글 객체 저장
        comment = postCommentRepository.saveAndFlush(comment);
        post.addComment(comment);
        post.plusCommentNum();

        // 생성된 댓글 정보 반환
        return CommentCreateResponse.toDTO(comment);
    }
}
