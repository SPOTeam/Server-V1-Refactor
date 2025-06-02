package com.example.spot.schedule.application;

import com.example.spot.schedule.presentation.dto.response.ScheduleResponseDTO;
import com.example.spot.schedule.presentation.dto.response.StudyQuizResponseDTO;
import com.example.spot.schedule.presentation.dto.response.StudyScheduleResponseDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ScheduleQueryService {

    // 특정 연월 일정 조회하기
    ScheduleResponseDTO.MonthlyScheduleListDTO getMonthlySchedules(Long studyId, int year, int month);

    // 일정 단건 조회하기
    ScheduleResponseDTO.MonthlyScheduleDTO getSchedule(Long studyId, Long scheduleId);

    // 다가오는 모임 일정 조회하기
    StudyScheduleResponseDTO findStudySchedule(Long studyId, Pageable pageable);

    // 금일 회원 출석 여부 조회하기
    StudyQuizResponseDTO.AttendanceListDTO getAllAttendances(Long studyId, Long scheduleId, LocalDate date);

    // 스터디 출석퀴즈 조회하기
    StudyQuizResponseDTO.QuizDTO getAttendanceQuiz(Long studyId, Long scheduleId, LocalDate date);
}
