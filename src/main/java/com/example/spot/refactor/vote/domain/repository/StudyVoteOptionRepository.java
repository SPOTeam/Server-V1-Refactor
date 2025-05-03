package com.example.spot.refactor.vote.domain.repository;

import com.example.spot.refactor.vote.domain.aggregate.StudyVoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyVoteOptionRepository extends JpaRepository<StudyVoteOption, Long> {

    Optional<StudyVoteOption> findByIdAndStudyVoteId(Long optionId, Long studyVoteId);

    List<StudyVoteOption> findAllByStudyVoteId(Long studyVoteId);
}
