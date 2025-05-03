package com.example.spot.refactor.schedule.domain.repository;

import com.example.spot.refactor.schedule.domain.StudySchedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Long> {

    List<StudySchedule> findAllByStudyId(Long studyId, Pageable pageable);

    List<StudySchedule> findByStudyId(Long studyId);

    Optional<StudySchedule> findByIdAndStudyId(Long id, Long studyId);

    Optional<StudySchedule> findByIdAndMemberId(Long scheduleId, Long memberId);
}
