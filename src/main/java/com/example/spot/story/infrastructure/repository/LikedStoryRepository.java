package com.example.spot.story.infrastructure.repository;

import com.example.spot.story.domain.entity.LikedStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikedStoryRepository extends JpaRepository<LikedStory, Long> {

    Optional<LikedStory> findByMemberIdAndStoryId(Long memberId, Long storyId);

    boolean existsByMemberIdAndStoryId(Long memberId, Long storyId);

    void deleteAllByStoryId(Long storyId);
}
