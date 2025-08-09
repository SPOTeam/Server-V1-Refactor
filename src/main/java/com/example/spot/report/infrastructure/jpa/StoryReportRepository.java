package com.example.spot.report.infrastructure.jpa;

import com.example.spot.report.domain.StoryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryReportRepository extends JpaRepository<StoryReport, Long> {

    void deleteAllByStoryId(Long storyId);
}
