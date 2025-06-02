package com.example.spot.study.application;

import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.study.presentation.dto.request.StudyHostWithdrawRequestDTO;
import com.example.spot.study.presentation.dto.request.StudyMemberReportDTO;
import com.example.spot.study.presentation.dto.request.StudyVoteRequestDTO;
import com.example.spot.story.web.dto.response.StoryResDTO;
import com.example.spot.study.presentation.dto.response.StudyTerminationResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyVoteResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyWithdrawalResponseDTO;
import com.example.spot.todo.presentation.dto.request.ToDoListRequestDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListCreateResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyApplyResponseDTO;
import jakarta.validation.Valid;

public interface StudyMemberCommandService {

    StudyWithdrawalResponseDTO.WithdrawalDTO withdrawFromStudy(Long studyId);
    StudyWithdrawalResponseDTO.WithdrawalDTO withdrawHostFromStudy(Long studyId, StudyHostWithdrawRequestDTO requestDTO);

    StudyTerminationResponseDTO.TerminationDTO terminateStudy(Long studyId, String performance);

    // 스터디 신청 수락
    StudyApplyResponseDTO acceptAndRejectStudyApply(Long memberId, Long studyId, boolean isAccept);

    StudyApplyResponseDTO acceptAndRejectStudyApplyForTest(Long memberId, Long studyId, boolean isAccept);

    // 스터디 투표 생성
    StudyVoteResponseDTO.VotePreviewDTO createVote(Long studyId, StudyVoteRequestDTO.VoteDTO voteDTO);

    // 스터디 투표 참여
    StudyVoteResponseDTO.VotedOptionDTO vote(Long studyId, Long voteId, StudyVoteRequestDTO.VotedOptionDTO votedOptionDTO);

    // 스터디 투표 수정
    StudyVoteResponseDTO.VotePreviewDTO updateVote(Long studyId, Long voteId, StudyVoteRequestDTO.VoteUpdateDTO voteDTO);

    // 스터디 투표 삭제
    StudyVoteResponseDTO.VotePreviewDTO deleteVote(Long studyId, Long voteId);

    // 스터디 회원 신고
    MemberResponseDTO.ReportedMemberDTO reportStudyMember(Long studyId, Long memberId, @Valid StudyMemberReportDTO studyMemberReportDTO);

    // 스터디 게시글 신고
    StoryResDTO.PostPreviewDTO reportStudyPost(Long studyId, Long postId);

    // 투두 리스트 생성
    ToDoListCreateResponseDTO createToDoList(Long studyId, ToDoListRequestDTO.ToDoListCreateDTO toDoListCreateDTO);

    // 투두 리스트 체크
    ToDoListResponseDTO.ToDoListUpdateResponseDTO checkToDoList(Long studyId, Long toDoListId);

    // 투두 리스트 수정
    ToDoListResponseDTO.ToDoListUpdateResponseDTO updateToDoList(Long studyId, Long toDoListId, ToDoListRequestDTO.ToDoListCreateDTO toDoListCreateDTO);

    // 투두 리스트 삭제
     ToDoListResponseDTO.ToDoListUpdateResponseDTO deleteToDoList(Long studyId, Long toDoListId);
}
