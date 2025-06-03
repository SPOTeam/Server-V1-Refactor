package com.example.spot.vote.domain.repository;

import com.example.spot.vote.domain.association.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {

    Optional<VoteOption> findByIdAndVoteId(Long optionId, Long studyVoteId);

    List<VoteOption> findAllByVoteId(Long studyVoteId);
}
