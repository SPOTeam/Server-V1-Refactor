package com.example.spot.refactor.story.domain.repository;

import com.example.spot.refactor.story.domain.aggregate.StudyPost;
import com.example.spot.refactor.story.domain.enums.StudyPostCategory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyPostRepository extends JpaRepository<StudyPost, Long>, StudyPostRepositoryCustom {

    Optional<StudyPost> findByStudyIdAndIsAnnouncement(Long studyId, boolean isAnnouncement);

    Optional<StudyPost> findByIdAndStudyId(Long postId, Long studyId);

    Optional<StudyPost> findByIdAndMemberId(Long postId, Long memberId);

    Long countByStudyId(Long studyId);

    Long countByStudyIdAndIsAnnouncement(Long studyId, Boolean aTrue);

    Long countByStudyIdAndStudyPostCategory(Long studyId, StudyPostCategory studyPostCategory);
}
