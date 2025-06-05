package com.example.spot.study.application;

import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;

public interface StudyMemberQueryService {

    // 참여하는 회원 목록 불러오기
    StudyMemberResponseDTO.StudyMemberListDTO findStudyMembers(Long studyId);

    // 스터디 별 신청 회원 목록 조회하기
    StudyMemberResponseDTO.StudyMemberListDTO findStudyApplicants(Long studyId);

    // 스터디 호스트 조회하기
    StudyMemberResponseDTO.HostDTO getStudyHost(Long studyId);

    // 스터디 신청 정보 가져오기
    StudyMemberResponseDTO.ApplyingMemberDTO findStudyApplication(Long studyId, Long memberId);

    // 스터디 신청 여부 확인
    StudyMemberResponseDTO.AppliedStudyDTO isApplied(Long studyId);

}
