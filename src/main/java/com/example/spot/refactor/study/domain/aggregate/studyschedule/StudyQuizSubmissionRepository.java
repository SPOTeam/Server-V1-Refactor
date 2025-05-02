package com.example.spot.refactor.study.domain.aggregate.studyschedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyQuizSubmissionRepository extends JpaRepository<StudyQuizSubmission, Long> {

    List<StudyQuizSubmission> findByStudyQuizId(Long studyQuizId);

    List<StudyQuizSubmission> findByStudyQuizIdAndMemberId(Long studyQuizId, Long id);
}
