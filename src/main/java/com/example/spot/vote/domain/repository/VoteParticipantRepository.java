package com.example.spot.vote.domain.repository;

import com.example.spot.vote.domain.association.VoteParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteParticipantRepository extends JpaRepository<VoteParticipant, Long> {

    boolean existsByMemberIdAndVoteOptionId(Long memberId, Long studyVoteOptionId);

    boolean existsByVoteOptionId(Long optionId);

    List<VoteParticipant> findAllByVoteOptionId(Long id);
}
