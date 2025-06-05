package com.example.spot.study.application;

import com.example.spot.study.presentation.dto.request.StudyMemberRequestDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyResponseDTO;

public interface StudyMemberCommandService {

    // 스터디 탈퇴
    StudyMemberResponseDTO.WithdrawalDTO withdrawFromStudy(Long studyId);

    // 스터디 호스트 탈퇴
    StudyMemberResponseDTO.WithdrawalDTO withdrawHostFromStudy(Long studyId, StudyMemberRequestDTO.HostWithdrawDTO hostWithdrawDTO);

    // 스터디 종료
    StudyResponseDTO.TerminationDTO terminateStudy(Long studyId, String performance);

    // 스터디 신청 수락
    StudyMemberResponseDTO.ApplicationStatusDTO acceptAndRejectStudyApply(Long memberId, Long studyId, boolean isAccept);

    // 스터디 승인 거절 테스트
    StudyMemberResponseDTO.ApplicationStatusDTO acceptAndRejectStudyApplyForTest(Long memberId, Long studyId, boolean isAccept);
}
