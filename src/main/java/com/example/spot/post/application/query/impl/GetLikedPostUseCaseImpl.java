package com.example.spot.post.application.query.impl;

import com.example.spot.post.application.query.GetLikedPostUseCase;
import com.example.spot.post.infrastructure.jpa.LikedPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.spot.common.security.utils.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetLikedPostUseCaseImpl implements GetLikedPostUseCase {

    private final LikedPostRepository likedPostRepository;

    /**
     * 게시글의 좋아요 수를 반환합니다.
     *
     * @param postId 게시글 ID
     * @return 게시글의 좋아요 수
     */
    @Override
    public long countByPostId(Long postId) {
        return likedPostRepository.countByPostId(postId);
    }

    /**
     * 현재 사용자의 게시글 좋아요 여부를 true/false로 반환합니다.
     *
     * @param postId 게시글 ID
     * @return 현재 사용자의 게시글 좋아요 여부
     */
    @Override
    public boolean existsByMemberIdAndPostId(Long postId) {
        Long currentUserId = getCurrentUserId();
        return likedPostRepository.existsByMemberIdAndPostId(currentUserId, postId);
    }
}
