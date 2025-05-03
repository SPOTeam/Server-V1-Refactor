package com.example.spot.refactor.schedule.domain;

import com.example.spot.refactor.schedule.domain.aggregate.StudyQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudyQuizRepository extends JpaRepository<StudyQuiz, Long> {

    List<StudyQuiz> findAllByStudyScheduleIdAndCreatedAtBetween(Long studyScheduleId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
