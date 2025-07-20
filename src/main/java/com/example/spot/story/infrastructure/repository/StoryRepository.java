package com.example.spot.story.infrastructure.repository;

import com.example.spot.story.domain.entity.Story;
import com.example.spot.story.domain.enums.StoryCategory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long>, StoryRepositoryCustom {

    Optional<Story> findByStudyIdAndIsAnnouncement(Long studyId, boolean isAnnouncement);

    Optional<Story> findByIdAndStudyId(Long postId, Long studyId);

    Optional<Story> findByIdAndMemberId(Long postId, Long memberId);

    Long countByStudyId(Long studyId);

    Long countByStudyIdAndIsAnnouncement(Long studyId, Boolean aTrue);

    Long countByStudyIdAndStoryCategory(Long studyId, StoryCategory storyCategory);
}
