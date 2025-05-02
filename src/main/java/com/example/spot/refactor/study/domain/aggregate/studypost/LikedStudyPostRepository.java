package com.example.spot.refactor.study.domain.aggregate.studypost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikedStudyPostRepository extends JpaRepository<LikedStudyPost, Long> {

    Optional<LikedStudyPost> findByMemberIdAndStudyPostId(Long memberId, Long postId);

    boolean existsByMemberIdAndStudyPostId(Long memberId, Long id);

    void deleteAllByStudyPostId(Long postId);
}
