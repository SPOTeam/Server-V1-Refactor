package com.example.spot.member.infrastructure;

import com.example.spot.member.domain.association.StudyJoinReason;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyJoinReasonRepository extends JpaRepository<StudyJoinReason, Long> {

    List<StudyJoinReason> findAllByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

}
