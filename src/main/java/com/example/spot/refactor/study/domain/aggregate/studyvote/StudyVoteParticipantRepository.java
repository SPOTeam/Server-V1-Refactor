package com.example.spot.refactor.study.domain.aggregate.studyvote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyVoteParticipantRepository extends JpaRepository<StudyVoteParticipant, Long> {

    boolean existsByMemberIdAndStudyVoteOptionId(Long memberId, Long studyVoteOptionId);

    boolean existsByStudyVoteOptionId(Long optionId);

    List<StudyVoteParticipant> findAllByStudyVoteOptionId(Long id);
}
