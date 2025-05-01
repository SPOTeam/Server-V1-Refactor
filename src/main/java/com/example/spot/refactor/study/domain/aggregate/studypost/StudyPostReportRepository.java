package com.example.spot.refactor.study.domain.aggregate.studypost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyPostReportRepository extends JpaRepository<StudyPostReport, Long> {

    void deleteAllByStudyPostId(Long postId);
}
