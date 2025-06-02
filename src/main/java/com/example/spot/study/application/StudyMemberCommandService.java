package com.example.spot.study.application;

import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.study.presentation.dto.request.StudyHostWithdrawRequestDTO;
import com.example.spot.study.presentation.dto.request.StudyMemberReportDTO;
import com.example.spot.vote.presentation.dto.request.StudyVoteRequestDTO;
import com.example.spot.story.web.dto.response.StoryResDTO;
import com.example.spot.study.presentation.dto.response.StudyTerminationResponseDTO;
import com.example.spot.vote.presentation.dto.response.StudyVoteResponseDTO;
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

    // 스터디 회원 신고
    MemberResponseDTO.ReportedMemberDTO reportStudyMember(Long studyId, Long memberId, @Valid StudyMemberReportDTO studyMemberReportDTO);

    // 스터디 게시글 신고
    StoryResDTO.PostPreviewDTO reportStudyPost(Long studyId, Long postId);

}
