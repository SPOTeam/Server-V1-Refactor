package com.example.spot.study.application;

import com.example.spot.study.presentation.dto.request.StudyMemberRequestDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyResponseDTO;
import jakarta.validation.Valid;

public interface StudyCommandService {

    // 스터디 참여 신청하기
    StudyMemberResponseDTO.JoinDTO applyToStudy(Long studyId, StudyMemberRequestDTO.@Valid JoinDTO studyJoinRequestDTO);

    // 스터디 등록하기
    StudyResponseDTO.RegisterDTO registerStudy(StudyMemberRequestDTO.RegisterDTO studyRegisterRequestDTO);

    // TODO 스터디 좋아요 Member 도메인 하위로 옮기기
    StudyResponseDTO.LikeDTO likeStudy(Long memberId, Long studyId);

    void addHotKeyword(String keyword);

    // 스터디 정보 수정
    StudyResponseDTO.RegisterDTO updateStudyInfo(Long studyId, StudyMemberRequestDTO.RegisterDTO studyInfoDTO);
}
