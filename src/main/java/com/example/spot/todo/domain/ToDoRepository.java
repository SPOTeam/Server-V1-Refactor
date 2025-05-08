package com.example.spot.todo.domain;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    Long countByStudyIdAndMemberIdAndDate(Long studyId, Long memberId, LocalDate date);
    List<ToDo> findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(Long studyId, Long memberId, LocalDate date, Pageable pageable);

}
