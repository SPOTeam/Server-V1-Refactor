package com.example.spot.member.domain.association;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyJoinReasonRepository extends JpaRepository<StudyJoinReason, Long> {

    boolean existsByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);

}
