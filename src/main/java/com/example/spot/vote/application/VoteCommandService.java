package com.example.spot.vote.application;

import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.story.web.dto.response.StoryResDTO;
import com.example.spot.study.presentation.dto.request.StudyHostWithdrawRequestDTO;
import com.example.spot.study.presentation.dto.request.StudyMemberReportDTO;
import com.example.spot.study.presentation.dto.response.StudyApplyResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyTerminationResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyWithdrawalResponseDTO;
import com.example.spot.todo.presentation.dto.request.ToDoListRequestDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListCreateResponseDTO;
import com.example.spot.vote.presentation.dto.request.StudyVoteRequestDTO;
import com.example.spot.vote.presentation.dto.response.StudyVoteResponseDTO;
import jakarta.validation.Valid;

public interface VoteCommandService {

    // 스터디 투표 생성
    StudyVoteResponseDTO.VotePreviewDTO createVote(Long studyId, StudyVoteRequestDTO.VoteDTO voteDTO);

    // 스터디 투표 참여
    StudyVoteResponseDTO.VotedOptionDTO vote(Long studyId, Long voteId, StudyVoteRequestDTO.VotedOptionDTO votedOptionDTO);

    // 스터디 투표 수정
    StudyVoteResponseDTO.VotePreviewDTO updateVote(Long studyId, Long voteId, StudyVoteRequestDTO.VoteUpdateDTO voteDTO);

    // 스터디 투표 삭제
    StudyVoteResponseDTO.VotePreviewDTO deleteVote(Long studyId, Long voteId);

}
