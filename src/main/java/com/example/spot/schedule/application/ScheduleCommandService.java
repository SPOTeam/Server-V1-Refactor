package com.example.spot.schedule.application;

import com.example.spot.schedule.presentation.dto.request.ScheduleRequestDTO;
import com.example.spot.schedule.presentation.dto.request.QuizRequestDTO;
import com.example.spot.schedule.presentation.dto.response.ScheduleResponseDTO;
import com.example.spot.schedule.presentation.dto.response.QuizResponseDTO;

import java.time.LocalDate;

public interface ScheduleCommandService {

    // 일정 생성
    ScheduleResponseDTO.ScheduleDTO addSchedule(Long studyId, ScheduleRequestDTO.ScheduleDTO scheduleRequestDTO);

    // 일정 수정
    ScheduleResponseDTO.ScheduleDTO modSchedule(Long studyId, Long scheduleId, ScheduleRequestDTO.ScheduleDTO scheduleModDTO);

    // 스터디 퀴즈 생성
    QuizResponseDTO.QuestionDTO createAttendanceQuiz(Long studyId, Long scheduleId, QuizRequestDTO.QuizDTO quizRequestDTO);

    // 스터디 출석
    QuizResponseDTO.AttendanceDTO attendantStudy(Long studyId, Long scheduleId, QuizRequestDTO.AttendanceDTO attendanceRequestDTO);

    // 스터디 퀴즈 삭제
    QuizResponseDTO.QuestionDTO deleteAttendanceQuiz(Long studyId, Long scheduleId, LocalDate date);
}
