package com.example.spot.refactor.study.application;

import com.example.spot.refactor.study.presentation.dto.request.StudyJoinRequestDTO;
import com.example.spot.refactor.study.presentation.dto.request.StudyRegisterRequestDTO;
import com.example.spot.refactor.study.presentation.dto.response.StudyJoinResponseDTO;
import com.example.spot.refactor.study.presentation.dto.response.StudyLikeResponseDTO;
import com.example.spot.refactor.study.presentation.dto.response.StudyRegisterResponseDTO;

public interface StudyCommandService {

    StudyJoinResponseDTO.JoinDTO applyToStudy(Long studyId, StudyJoinRequestDTO.StudyJoinDTO studyJoinRequestDTO);

    StudyRegisterResponseDTO.RegisterDTO registerStudy(StudyRegisterRequestDTO.RegisterDTO studyRegisterRequestDTO);


    // TODO 스터디 좋아요 Member 도메인 하위로 옮기기
    StudyLikeResponseDTO likeStudy(Long memberId, Long studyId);

    void addHotKeyword(String keyword);

    // 스터디 정보 수정
    StudyRegisterResponseDTO.RegisterDTO updateStudyInfo(Long studyId, StudyRegisterRequestDTO.RegisterDTO studyInfoDTO);
}
