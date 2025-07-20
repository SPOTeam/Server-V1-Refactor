package com.example.spot.story.infrastructure.repository;

import com.example.spot.story.domain.entity.StoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StoryImageRepository extends JpaRepository<StoryImage, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM StoryImage spi WHERE spi.story.id = :storyId")
    void deleteAllByStoryId(@Param("storyId") Long storyId);
}
