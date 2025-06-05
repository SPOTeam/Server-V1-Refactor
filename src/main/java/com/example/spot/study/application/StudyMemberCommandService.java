package com.example.spot.study.application;

import com.example.spot.study.presentation.dto.request.StudyHostWithdrawRequestDTO;
import com.example.spot.study.presentation.dto.response.StudyTerminationResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyWithdrawalResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyApplyResponseDTO;

public interface StudyMemberCommandService {

    // 스터디 탈퇴
    StudyWithdrawalResponseDTO.WithdrawalDTO withdrawFromStudy(Long studyId);

    // 스터디 호스트 탈퇴
    StudyWithdrawalResponseDTO.WithdrawalDTO withdrawHostFromStudy(Long studyId, StudyHostWithdrawRequestDTO requestDTO);

    // 스터디 종료
    StudyTerminationResponseDTO.TerminationDTO terminateStudy(Long studyId, String performance);

    // 스터디 신청 수락
    StudyApplyResponseDTO acceptAndRejectStudyApply(Long memberId, Long studyId, boolean isAccept);

    // 스터디 승인 거절 테스트
    StudyApplyResponseDTO acceptAndRejectStudyApplyForTest(Long memberId, Long studyId, boolean isAccept);
}
