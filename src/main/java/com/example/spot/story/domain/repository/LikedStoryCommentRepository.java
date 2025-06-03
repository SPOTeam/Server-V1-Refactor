package com.example.spot.story.domain.repository;

import com.example.spot.story.domain.association.LikedStoryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikedStoryCommentRepository extends JpaRepository<LikedStoryComment, Long> {

    Optional<LikedStoryComment> findByMemberIdAndStoryCommentId(Long memberId, Long commentId);
    Optional<LikedStoryComment> findByMemberIdAndStoryCommentIdAndIsLiked(Long memberId, Long commentId, Boolean isLiked);
}
