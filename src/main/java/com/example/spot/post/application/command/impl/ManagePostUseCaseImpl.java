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
import com.example.spot.post.domain.PostStats;
import com.example.spot.post.domain.enums.Board;
import com.example.spot.post.infrastructure.jpa.PostRepository;
import com.example.spot.post.infrastructure.jpa.PostStatsRepository;
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
    private final PostStatsRepository postStatsRepository;
    
    private final S3ImageService s3ImageService;

    @Override
    public PostCreateResponse createPost(Long memberId, PostCreateRequest req) {
        Member member = requireMemberRef(memberId);
        ensureAnnouncementPermission(req.getType(), member);

        List<String> imageUrls = uploadImageIfPresent(req.getImage());

        Post post = postRepository.save(buildPost(req, member, imageUrls));
        postStatsRepository.save(
                PostStats.builder()
                        .post(post)
                        .likeCount(0L)
                        .commentCount(0L)
                        .build()
        );

        return PostCreateResponse.toDTO(post);
    }

    @Override
    public PostCreateResponse updatePost(Long memberId, Long postId, PostUpdateRequest req) {
        ensurePostAndMemberExist(postId, memberId);

        Member member = requireMemberRef(memberId);
        Post post = requirePostRef(postId);

        ensurePostAuthor(post, member);
        ensureAnnouncementPermission(req.getType(), member);

        post.edit(req, uploadImageIfPresent(req.getImage()), req.getExistingImage());
        return PostCreateResponse.toDTO(post);
    }

    @Override
    public void deletePost(Long memberId, Long postId) {
        ensurePostAndMemberExist(postId, memberId);

        Member member = requireMemberRef(memberId);
        Post post = requirePostRef(postId);

        ensurePostAuthor(post, member);
        postRepository.delete(post);
    }

    /* ------------------------------- helpers ------------------------------------------ */

    private void ensurePostAndMemberExist(Long postId, Long memberId) {
        if (!postRepository.existsById(postId)) {
            throw new PostHandler(ErrorStatus._POST_NOT_FOUND);
        }
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

    private void ensureAnnouncementPermission(Board board, Member member) {
        if (board == Board.SPOT_ANNOUNCEMENT && !member.getIsAdmin()) {
            throw new PostHandler(ErrorStatus._FORBIDDEN);
        }
    }

    private void ensurePostAuthor(Post post, Member member) {
        if (!post.getMember().getId().equals(member.getId())) {
            throw new PostHandler(ErrorStatus._POST_NOT_AUTHOR);
        }
    }

    private List<String> uploadImageIfPresent(MultipartFile imageFile) {
        if (imageFile == null) {
            return List.of();
        }
        ImageUploadResponse res = s3ImageService.uploadImages(List.of(imageFile));
        return res.getImageUrls().stream().map(Images::getImageUrl).toList();
    }

    private Post buildPost(PostCreateRequest req, Member author, List<String> images) {
        String image = (images != null && !images.isEmpty() && !images.get(0).isEmpty()) ? images.get(0) : null;
        return Post.builder()
                .isAnonymous(req.isAnonymous())
                .title(req.getTitle())
                .content(req.getContent())
                .image(image)
                .board(req.getType())
                .member(author)
                .build();
    }
}
