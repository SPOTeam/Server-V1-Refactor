package com.example.spot.vote.application;

import com.example.spot.schedule.presentation.dto.response.ScheduleResponseDTO;
import com.example.spot.schedule.presentation.dto.response.StudyQuizResponseDTO;
import com.example.spot.schedule.presentation.dto.response.StudyScheduleResponseDTO;
import com.example.spot.story.web.dto.response.StoryResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyImageResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO.StudyApplicantDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO;
import com.example.spot.vote.presentation.dto.response.StudyVoteResponseDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

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
