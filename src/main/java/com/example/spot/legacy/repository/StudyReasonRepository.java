package com.example.spot.legacy.repository;

import com.example.spot.legacy.domain.StudyReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyReasonRepository extends JpaRepository<StudyReason, Long> {

    boolean existsByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);

}
