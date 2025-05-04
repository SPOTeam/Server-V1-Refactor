package com.example.spot.refactor.schedule.domain.repository;

import com.example.spot.refactor.schedule.domain.aggregate.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByScheduleIdAndCreatedAtBetween(Long scheduleId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
