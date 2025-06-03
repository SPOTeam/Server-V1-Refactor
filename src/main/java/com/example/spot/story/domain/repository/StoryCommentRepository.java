package com.example.spot.story.domain.repository;

import com.example.spot.story.domain.association.StoryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryCommentRepository extends JpaRepository<StoryComment, Long> {

    List<StoryComment> findAllByMemberIdAndStoryId(Long memberId, Long storyId);

    List<StoryComment> findAllByStoryId(Long storyId);

    void deleteAllByStoryId(Long storyId);
}
