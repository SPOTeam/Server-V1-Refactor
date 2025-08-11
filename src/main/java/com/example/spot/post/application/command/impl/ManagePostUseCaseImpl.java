package com.example.spot.post.application.command.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.PostHandler;
import com.example.spot.common.application.s3.S3ImageService;
import com.example.spot.common.presentation.dto.util.response.ImageResponse.ImageUploadResponse;
import com.example.spot.common.presentation.dto.util.response.ImageResponse.Images;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.post.application.command.ManagePostUseCase;
import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.enums.Board;
import com.example.spot.post.infrastructure.jpa.PostRepository;
import com.example.spot.post.presentation.dto.request.post.PostCreateRequest;
import com.example.spot.post.presentation.dto.request.post.PostUpdateRequest;
import com.example.spot.post.presentation.dto.response.post.PostCreateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagePostUseCaseImpl implements ManagePostUseCase {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    private final S3ImageService s3ImageService;

    /**
     * 게시글을 생성합니다.
     *
     * @param memberId          게시글을 작성하는 회원 ID
     * @param postCreateRequest 생성할 게시글 정보
     * @return 생성된 게시글 정보
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   관리자 권한이 없는 경우 (관리자만 공지글 작성 가능)
     */
    @Transactional
    @Override
    public PostCreateResponse createPost(Long memberId, PostCreateRequest postCreateRequest) {

        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 공지"SPOT_ANNOUNCEMENT" 게시글은 관리자만 생성 가능
        if (postCreateRequest.getType() == Board.SPOT_ANNOUNCEMENT && !member.getIsAdmin()) {
            throw new PostHandler(ErrorStatus._FORBIDDEN); // 관리자만 접근 가능
        }

        List<String> imageUrls = getImageUrls(postCreateRequest.getImage());

        // Post 객체 생성 및 연관 관계 설정
        Post post = createPostEntity(postCreateRequest, member, imageUrls);

        // 게시글 저장
        post = postRepository.save(post);

        // 게시글 생성 정보 반환
        return PostCreateResponse.toDTO(post);
    }

    private List<String> getImageUrls(MultipartFile imageFile) {
        if (imageFile != null) {
            ImageUploadResponse imageUploadResponse = s3ImageService.uploadImages(List.of(imageFile));

            return imageUploadResponse.getImageUrls().stream()
                    .map(Images::getImageUrl)
                    .toList();
        }
        return List.of();
    }

    /**
     * 게시글 객체를 생성합니다.
     *
     * @param postCreateRequest 생성할 게시글 정보
     * @param currentMember     게시글을 작성하는 회원 정보
     * @return 생성된 게시글 객체
     */
    private Post createPostEntity(PostCreateRequest postCreateRequest, Member currentMember, List<String> images) {
        String image = (images != null && !images.isEmpty() && !images.get(0).isEmpty())
                ? images.get(0)
                : null;

        return Post.builder()
                .isAnonymous(postCreateRequest.isAnonymous())
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .image(image)
                .board(postCreateRequest.getType())
                .member(currentMember)
                .build();
    }

    /**
     * 게시글을 수정합니다.
     *
     * @param memberId          게시글을 수정하는 회원 ID
     * @param postId            변경할 게시글 ID
     * @param postUpdateRequest 수정할 게시글 정보
     * @return 수정된 게시글 정보
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   게시글을 찾을 수 없는 경우
     * @throws PostHandler   현재 수정하는 회원과 게시글 작성자가 일치하지 않을 경우
     * @throws PostHandler   관리자 권한이 없는 경우 (관리자만 공지글 수정 가능)
     */
    @Transactional
    @Override
    public PostCreateResponse updatePost(Long memberId, Long postId, PostUpdateRequest postUpdateRequest) {

        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_FOUND));

        // 현재 멤버와 게시글 작성자 일치 여부 확인
        if (!post.getMember().getId().equals(member.getId())) {
            throw new PostHandler(ErrorStatus._POST_NOT_AUTHOR);
        }

        // 공지"SPOT_ANNOUNCEMENT" 게시글은 관리자만 가능
        if (postUpdateRequest.getType() == Board.SPOT_ANNOUNCEMENT && !member.getIsAdmin()) {
            throw new PostHandler(ErrorStatus._FORBIDDEN);
        }

        // 게시글 수정
        post.edit(postUpdateRequest, getImageUrls(postUpdateRequest.getImage()), postUpdateRequest.getExistingImage());

        // 수정된 게시글 정보 반환
        return PostCreateResponse.toDTO(post);
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param memberId 게시글을 수정하는 회원 ID
     * @param postId   변경할 게시글 ID
     * @throws MemberHandler 회원을 찾을 수 없는 경우
     * @throws PostHandler   게시글을 찾을 수 없는 경우
     * @throws PostHandler   현재 삭제하는 회원과 게시글 작성자가 일치하지 않을 경우
     */
    @Transactional
    @Override
    public void deletePost(Long memberId, Long postId) {

        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_FOUND));

        // 현재 멤버와 게시글 작성자 일치 여부 확인
        if (!post.getMember().getId().equals(member.getId())) {
            throw new PostHandler(ErrorStatus._POST_NOT_AUTHOR); // 권한 없음을 나타내는 에러 처리
        }
        // 게시글 삭제
        postRepository.delete(post);
    }
}
