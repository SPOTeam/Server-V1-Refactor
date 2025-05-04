package com.example.spot.refactor.study.domain.repository;

import com.example.spot.refactor.study.domain.aggregate.StudyMember;
import com.example.spot.refactor.study.domain.enums.StudyApplicationStatus;
import com.example.spot.refactor.member.domain.enums.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    List<StudyMember> findByMemberIdAndStatusNot(Long memberId, StudyApplicationStatus status);

    List<StudyMember> findAllByMemberIdAndStatus(Long memberId, StudyApplicationStatus status);

    List<StudyMember> findAllByMemberIdAndIsOwned(Long memberId, Boolean isOwned);

    List<StudyMember> findAllByStudyIdAndStatus(Long studyId, StudyApplicationStatus status);

    Optional<StudyMember> findByMemberIdAndStudyIdAndStatus(Long memberId, Long studyId, StudyApplicationStatus status);

    Optional<StudyMember> findByMemberIdAndStudyIdAndIsOwned(Long memberId, Long studyId, Boolean isOwned);

    Optional<StudyMember> findByMemberIdAndStudyId(Long memberId, Long studyId);

    long countByStatusAndStudyId(StudyApplicationStatus status, Long studyId);
    long countByMemberIdAndStatusAndStudy_Status(Long memberId, StudyApplicationStatus studyApplicationStatus, Status status);
    long countByMemberIdAndIsOwnedAndStudy_Status(Long memberId, Boolean isOwned, Status status);

    boolean existsByMemberIdAndStudyIdAndStatus(Long memberId, Long studyId, StudyApplicationStatus studyApplicationStatus);

    Optional<StudyMember> findByStudyIdAndIsOwned(Long studyId, boolean b);

    boolean existsByMemberIdAndIsOwned(Long memberId, boolean b);
}
