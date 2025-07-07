package com.example.spot.member.infrastructure;

import com.example.spot.member.domain.association.StudyJoinReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyJoinReasonRepository extends JpaRepository<StudyJoinReason, Long> {

    boolean existsByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

}
