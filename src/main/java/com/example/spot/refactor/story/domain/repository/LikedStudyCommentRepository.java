package com.example.spot.refactor.story.domain.repository;

import com.example.spot.refactor.story.domain.aggregate.LikedStudyComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikedStudyCommentRepository extends JpaRepository<LikedStudyComment, Long> {

    Optional<LikedStudyComment> findByMemberIdAndStudyPostCommentId(Long memberId, Long commentId);
    Optional<LikedStudyComment> findByMemberIdAndStudyPostCommentIdAndIsLiked(Long memberId, Long commentId, Boolean isLiked);
}
