package com.example.spot.refactor.study.domain.aggregate.studytodo;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyToDoRepository extends JpaRepository<StudyToDo, Long> {
    Long countByStudyIdAndMemberIdAndDate(Long studyId, Long memberId, LocalDate date);
    List<StudyToDo> findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(Long studyId, Long memberId, LocalDate date, Pageable pageable);

}
