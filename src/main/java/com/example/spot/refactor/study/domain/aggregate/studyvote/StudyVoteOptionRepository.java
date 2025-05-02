package com.example.spot.refactor.study.domain.aggregate.studyvote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyVoteOptionRepository extends JpaRepository<StudyVoteOption, Long> {

    Optional<StudyVoteOption> findByIdAndStudyVoteId(Long optionId, Long studyVoteId);

    List<StudyVoteOption> findAllByStudyVoteId(Long studyVoteId);
}
