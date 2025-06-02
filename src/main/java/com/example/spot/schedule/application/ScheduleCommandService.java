package com.example.spot.schedule.application;

import com.example.spot.schedule.presentation.dto.request.ScheduleRequestDTO;
import com.example.spot.schedule.presentation.dto.request.StudyQuizRequestDTO;
import com.example.spot.schedule.presentation.dto.response.ScheduleResponseDTO;
import com.example.spot.schedule.presentation.dto.response.StudyQuizResponseDTO;

import java.time.LocalDate;

public interface ScheduleCommandService {

    // 일정 생성
    ScheduleResponseDTO.ScheduleDTO addSchedule(Long studyId, ScheduleRequestDTO.ScheduleDTO scheduleRequestDTO);

    // 일정 수정
    ScheduleResponseDTO.ScheduleDTO modSchedule(Long studyId, Long scheduleId, ScheduleRequestDTO.ScheduleDTO scheduleModDTO);

    // 스터디 퀴즈 생성
    StudyQuizResponseDTO.QuizDTO createAttendanceQuiz(Long studyId, Long scheduleId, StudyQuizRequestDTO.QuizDTO quizRequestDTO);

    // 스터디 출석
    StudyQuizResponseDTO.AttendanceDTO attendantStudy(Long studyId, Long scheduleId, StudyQuizRequestDTO.AttendanceDTO attendanceRequestDTO);

    // 스터디 퀴즈 삭제
    StudyQuizResponseDTO.QuizDTO deleteAttendanceQuiz(Long studyId, Long scheduleId, LocalDate date);
}
