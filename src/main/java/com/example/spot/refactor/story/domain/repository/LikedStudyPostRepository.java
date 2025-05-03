package com.example.spot.refactor.story.domain.repository;

import com.example.spot.refactor.story.domain.aggregate.LikedStudyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikedStudyPostRepository extends JpaRepository<LikedStudyPost, Long> {

    Optional<LikedStudyPost> findByMemberIdAndStudyPostId(Long memberId, Long postId);

    boolean existsByMemberIdAndStudyPostId(Long memberId, Long id);

    void deleteAllByStudyPostId(Long postId);
}
