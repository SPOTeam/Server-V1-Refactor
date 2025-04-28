package com.example.spot.legacy.repository;

import com.example.spot.legacy.domain.mapping.MemberAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberAttendanceRepository extends JpaRepository<MemberAttendance, Long> {

    List<MemberAttendance> findByQuizId(Long quizId);

    List<MemberAttendance> findByQuizIdAndMemberId(Long quizId, Long id);
}
