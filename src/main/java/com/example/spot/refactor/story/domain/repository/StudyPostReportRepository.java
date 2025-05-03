package com.example.spot.refactor.story.domain.repository;

import com.example.spot.refactor.story.domain.aggregate.StudyPostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyPostReportRepository extends JpaRepository<StudyPostReport, Long> {

    void deleteAllByStudyPostId(Long postId);
}
