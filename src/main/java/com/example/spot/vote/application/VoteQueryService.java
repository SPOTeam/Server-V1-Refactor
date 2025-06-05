package com.example.spot.vote.application;

import com.example.spot.vote.presentation.dto.response.StudyVoteResponseDTO;

public interface VoteQueryService {

    // 스터디 투표 목록 조회
    StudyVoteResponseDTO.VoteListDTO getAllVotes(Long studyId);

    // 스터디 투표 마감 여부 조회
    Boolean getIsCompleted(Long voteId);

    // 스터디 투표(진행중) 조회
    StudyVoteResponseDTO.VoteDTO getVoteInProgress(Long studyId, Long voteId);

    // 스터디 투표(마감) 조회
    StudyVoteResponseDTO.CompletedVoteDTO getVoteInCompletion(Long studyId, Long voteId);

    // 스터디 투표 현황 조회
    StudyVoteResponseDTO.CompletedVoteDetailDTO getCompletedVoteDetail(Long studyId, Long voteId);
}
