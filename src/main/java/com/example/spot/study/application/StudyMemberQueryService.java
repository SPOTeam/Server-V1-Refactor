package com.example.spot.study.application;

import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO;
import com.example.spot.schedule.presentation.dto.response.ScheduleResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyImageResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResDTO;
import com.example.spot.schedule.presentation.dto.response.StudyQuizResponseDTO;
import com.example.spot.vote.presentation.dto.response.StudyVoteResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.story.web.dto.response.StoryResponseDTO;
import com.example.spot.schedule.presentation.dto.response.StudyScheduleResponseDTO;


import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO.StudyApplicantDTO;
import java.time.LocalDate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface StudyMemberQueryService {

    // 참여하는 회원 목록 불러오기
    StudyMemberResponseDTO findStudyMembers(Long studyId);

    // 스터디 별 신청 회원 목록 조회하기
    StudyMemberResponseDTO findStudyApplicants(Long studyId);

    // 스터디 호스트 조회하기
    StudyMemberResDTO.StudyHostDTO getStudyHost(Long studyId);

    // 스터디 신청 정보 가져오기
    StudyMemberResponseDTO.StudyApplyMemberDTO findStudyApplication(Long studyId, Long memberId);

    // 스터디 신청 여부 확인
    StudyApplicantDTO isApplied(Long studyId);

}
