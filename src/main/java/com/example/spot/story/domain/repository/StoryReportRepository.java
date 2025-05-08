package com.example.spot.story.domain.repository;

import com.example.spot.story.domain.aggregate.StoryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryReportRepository extends JpaRepository<StoryReport, Long> {

    void deleteAllByStoryId(Long storyId);
}
