package com.example.spot.refactor.study.domain.aggregate.studyvote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyVoteRepository extends JpaRepository<StudyVote, Long> {

    Optional<StudyVote> findByIdAndStudyId(Long voteId, Long studyId);

    // 진행중인 투표 목록
    List<StudyVote> findAllByStudyIdAndFinishedAtAfter(Long studyId, LocalDateTime now);

    // 마감된 투표 목록
    List<StudyVote> findAllByStudyIdAndFinishedAtBefore(Long studyId, LocalDateTime now);

    // 투표 마감 여부
    Boolean existsByIdAndFinishedAtBefore(Long voteId, LocalDateTime now);
}
