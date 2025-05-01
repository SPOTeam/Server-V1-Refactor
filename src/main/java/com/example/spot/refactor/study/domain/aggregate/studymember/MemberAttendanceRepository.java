package com.example.spot.refactor.study.domain.aggregate.studymember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberAttendanceRepository extends JpaRepository<MemberAttendance, Long> {

    List<MemberAttendance> findByQuizId(Long quizId);

    List<MemberAttendance> findByQuizIdAndMemberId(Long quizId, Long id);
}
