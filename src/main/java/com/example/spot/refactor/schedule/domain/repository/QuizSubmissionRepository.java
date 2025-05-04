package com.example.spot.refactor.schedule.domain.repository;

import com.example.spot.refactor.schedule.domain.aggregate.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    List<QuizSubmission> findByQuizId(Long quizId);

    List<QuizSubmission> findByQuizIdAndMemberId(Long quizId, Long id);
}
