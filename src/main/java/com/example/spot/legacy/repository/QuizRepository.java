package com.example.spot.legacy.repository;

import com.example.spot.legacy.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByScheduleId(Long scheduleId);

    List<Quiz> findAllByScheduleIdAndCreatedAtBetween(Long scheduleId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
