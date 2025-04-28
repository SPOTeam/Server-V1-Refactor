package com.example.spot.legacy.repository;

import com.example.spot.legacy.domain.study.StudyPostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyPostReportRepository extends JpaRepository<StudyPostReport, Long> {

    void deleteAllByStudyPostId(Long postId);
}
