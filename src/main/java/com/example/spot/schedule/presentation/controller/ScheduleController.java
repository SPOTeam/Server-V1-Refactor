package com.example.spot.schedule.presentation.controller;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.presentation.validator.IntSize;
import com.example.spot.schedule.application.ScheduleCommandService;
import com.example.spot.schedule.application.ScheduleQueryService;
import com.example.spot.schedule.domain.validation.annotation.ExistSchedule;
import com.example.spot.schedule.presentation.dto.request.ScheduleRequestDTO;
import com.example.spot.schedule.presentation.dto.request.QuizRequestDTO;
import com.example.spot.schedule.presentation.dto.response.ScheduleResponseDTO;
import com.example.spot.schedule.presentation.dto.response.QuizResponseDTO;
import com.example.spot.study.domain.validation.annotation.ExistStudy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spot")
@Validated
public class ScheduleController {

    private final ScheduleQueryService scheduleQueryService;
    private final ScheduleCommandService scheduleCommandService;

    /* ----------------------------- 스터디 일정 관련 API ------------------------------------- */

    @Tag(name = "스터디 일정")
    @Operation(summary = "[스터디 일정] 월별 일정 불러오기", description = """ 
            ## [스터디 일정] 내 스터디 > 스터디 > 캘린더 클릭, 로그인한 회원이 참여하는 특정 스터디의 일정을 월 단위로 불러옵니다.
            처음 캘린더를 클릭하면 오늘 날짜가 포함된 연/월에 해당하는 일정 목록이 schedule에서 반환됩니다.
            캘린더를 넘기면 해당 연/월에 해당하는 일정 목록이 schedule에서 반환됩니다.
            """)
    @Parameter(name = "studyId", description = "일정을 불러올 스터디의 id를 입력합니다.", required = true)
    @GetMapping("/studies/{studyId}/schedules")
    public ApiResponse<ScheduleResponseDTO.MonthlyScheduleListDTO> getMonthlySchedules(
            @PathVariable @ExistStudy Long studyId,
            @RequestParam @IntSize(min = 1) Integer year,
            @RequestParam @IntSize(min = 1, max = 12) Integer month) {
        ScheduleResponseDTO.MonthlyScheduleListDTO monthlyScheduleDTO = scheduleQueryService.getMonthlySchedules(studyId, year, month);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_SCHEDULE_FOUND, monthlyScheduleDTO);
    }

    @Tag(name = "스터디 일정")
    @Operation(summary = "[스터디 일정] 상세 일정 불러오기", description = """ 
            ## [스터디 일정] 내 스터디 > 스터디 > 캘린더 > 일정 클릭, 로그인한 회원이 참여하는 특정 스터디의 상세 일정을 불러옵니다.
            스터디의 일정 정보를 상세하게 불러옵니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "scheduleId", description = "불러올 스터디 일정의 id를 입력합니다.", required = true)
    @GetMapping("/studies/{studyId}/schedules/{scheduleId}")
    public ApiResponse<ScheduleResponseDTO.MonthlyScheduleDTO> getSchedule(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistSchedule Long scheduleId) {
        ScheduleResponseDTO.MonthlyScheduleDTO scheduleDTO = scheduleQueryService.getSchedule(studyId, scheduleId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_SCHEDULE_FOUND, scheduleDTO);
    }

    @Tag(name = "스터디 일정")
    @Operation(summary = "[스터디 일정] 일정 추가하기", description = """ 
            ## [스터디 일정] 내 스터디 > 스터디 > 캘린더 > 추가 버튼 클릭, 로그인한 회원이 운영하는 특정 스터디에 일정을 추가합니다.
            로그인한 회원이 owner인 경우 schedule에 새로운 일정을 등록합니다.
            
            period에는 [NONE, DAILY, WEEKLY, BIWEEKLY, MONTHLY] 중 하나를 입력해야 합니다.
            """)
    @Parameter(name = "studyId", description = "일정을 추가할 스터디의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/schedules")
    public ApiResponse<ScheduleResponseDTO.ScheduleDTO> addSchedule(
            @PathVariable @ExistStudy Long studyId,
            @RequestBody @Valid ScheduleRequestDTO.ScheduleDTO scheduleRequestDTO) {
        ScheduleResponseDTO.ScheduleDTO scheduleResponseDTO = scheduleCommandService.addSchedule(studyId, scheduleRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_SCHEDULE_CREATED, scheduleResponseDTO);
    }

    @Tag(name = "스터디 일정")
    @Operation(summary = "[스터디 일정] 일정 변경하기", description = """ 
            ## [스터디 일정] 내 스터디 > 스터디 > 캘린더 > 일정 클릭, 로그인한 회원이 특정 스터디에 등록한 일정을 수정합니다.
            로그인한 회원이 owner인 경우 schedule에 등록한 일정을 수정할 수 있습니다.
            
            period에는 [NONE, DAILY, WEEKLY, BIWEEKLY, MONTHLY] 중 하나를 입력해야 합니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "scheduleId", description = "변경할 일정의 id를 입력합니다.", required = true)
    @PatchMapping("/studies/{studyId}/schedules/{scheduleId}")
    public ApiResponse<ScheduleResponseDTO.ScheduleDTO> modSchedule(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistSchedule Long scheduleId,
            @RequestBody @Valid ScheduleRequestDTO.ScheduleDTO scheduleModDTO) {
        ScheduleResponseDTO.ScheduleDTO scheduleResponseDTO = scheduleCommandService.modSchedule(studyId, scheduleId, scheduleModDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_SCHEDULE_UPDATED, scheduleResponseDTO);
    }

    @Tag(name = "스터디 일정")
    @Operation(summary = "[스터디 일정] 다가오는 모임 목록 조회하기", description = """ 
        ## [스터디 일정] 내 스터디 > 스터디 클릭, 로그인한 회원이 참여하는 특정 스터디의 다가오는 모임 목록을 페이징 조회 합니다.
        현재 시점 이후에 진행되는 모임 일정의 목록을 schedule에서 반환합니다.
        """)
    @GetMapping("/studies/{studyId}/upcoming-schedules")
    public ApiResponse<ScheduleResponseDTO.SchedulePageDTO> getUpcomingSchedules(
            @PathVariable @ExistStudy Long studyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size){
        ScheduleResponseDTO.SchedulePageDTO studyScheduleResponseDTO = scheduleQueryService.findStudySchedule(studyId, PageRequest.of(page, size));
        return ApiResponse.onSuccess(SuccessStatus._STUDY_SCHEDULE_FOUND, studyScheduleResponseDTO);
    }


    /* ----------------------------- 스터디 출석체크 관련 API ------------------------------------- */

    @Tag(name = "스터디 출석체크")
    @Operation(summary = "[스터디 출석체크] 출석 퀴즈 생성하기", description = """ 
            ## [스터디 출석체크] 내 스터디 > 스터디 > 캘린더 > 출석체크 > 퀴즈 만들기 클릭, 로그인한 회원이 운영하는 스터디에 퀴즈를 생성합니다.
            * 로그인한 회원이 스터디장인 경우 quiz에 새로운 퀴즈를 생성합니다.
            * createdAt에는 출석 퀴즈를 생성할 날짜를 입력합니다.
            """)
    @Parameter(name = "studyId", description = "출석 퀴즈를 생성할 스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "scheduleId", description = "출석 퀴즈를 생성할 일정의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/schedules/{scheduleId}/quiz")
    public ApiResponse<QuizResponseDTO.QuestionDTO> createAttendanceQuiz(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistSchedule Long scheduleId,
            @RequestBody @Valid QuizRequestDTO.QuizDTO quizRequestDTO) {
        QuizResponseDTO.QuestionDTO quizResponseDTO = scheduleCommandService.createAttendanceQuiz(studyId, scheduleId, quizRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_QUIZ_CREATED, quizResponseDTO);
    }

    @Tag(name = "스터디 출석체크")
    @Operation(summary = "[스터디 출석체크] 출석 퀴즈 불러오기", description = """ 
            ## [스터디 출석체크] 내 스터디 > 스터디 > 캘린더 > 출석체크, 로그인한 회원이 참여하는 스터디의 퀴즈를 불러옵니다.
            * 날짜에 해당하는 퀴즈의 아이디와 질문이 반환됩니다.
            * date에는 출석 퀴즈를 불러올 날짜를 입력합니다.
            """)
    @Parameter(name = "studyId", description = "출석 퀴즈를 불러올 스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "scheduleId", description = "출석 퀴즈를 불러올 일정의 id를 입력합니다.", required = true)
    @GetMapping("/studies/{studyId}/schedules/{scheduleId}/quiz")
    public ApiResponse<QuizResponseDTO.QuestionDTO> getAttendanceQuiz(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistSchedule Long scheduleId,
            @RequestParam LocalDate date) {
        QuizResponseDTO.QuestionDTO questionDTO = scheduleQueryService.getAttendanceQuiz(studyId, scheduleId, date);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_QUIZ_FOUND, questionDTO);
    }


    @Tag(name = "스터디 출석체크")
    @Operation(summary = "[스터디 출석체크] 출석 체크하기", description = """ 
            ## [스터디 출석체크] 내 스터디 > 스터디 > 캘린더 > 이미지 클릭, 로그인한 회원이 참여하는 스터디에서 오늘의 퀴즈를 풀어 출석을 체크합니다.
            * 특정 시점의 quiz에 대해 member_attendance 튜플을 추가합니다.
            * dateTime에는 출석을 체크할 날짜와 시간을 입력합니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "scheduleId", description = "일정의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/schedules/{scheduleId}/attendance")
    public ApiResponse<QuizResponseDTO.AttendanceDTO> attendantStudy(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistSchedule Long scheduleId,
            @RequestBody @Valid QuizRequestDTO.AttendanceDTO attendanceRequestDTO) {
        QuizResponseDTO.AttendanceDTO attendanceResponseDTO = scheduleCommandService.attendantStudy(studyId, scheduleId, attendanceRequestDTO);
        if (attendanceResponseDTO.getIsCorrect()) {
            return ApiResponse.onSuccess(SuccessStatus._STUDY_ATTENDANCE_CREATED_CORRECT_ANSWER, attendanceResponseDTO);
        } else {
            return ApiResponse.onSuccess(SuccessStatus._STUDY_ATTENDANCE_CREATED_WRONG_ANSWER, attendanceResponseDTO);
        }
    }

    @Tag(name = "스터디 출석체크")
    @Operation(summary = "[스터디 출석체크] 출석 퀴즈 삭제하기", description = """ 
            ## [스터디 출석체크] 기한이 지난 출석 퀴즈를 삭제합니다. (화면 X)
            * PathVariable을 통해 전달받은 정보를 바탕으로 출석 퀴즈를 삭제합니다.
            * 출석 퀴즈 정보와 함께 퀴즈에 대한 MemberAttendance(회원 출석) 목록도 함께 삭제됩니다.
            * date에는 출석 퀴즈를 삭제할 날짜를 입력합니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "scheduleId", description = "일정의 id를 입력합니다.", required = true)
    @DeleteMapping("/studies/{studyId}/schedules/{scheduleId}/quiz")
    public ApiResponse<QuizResponseDTO.QuestionDTO> deleteAttendanceQuiz(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistSchedule Long scheduleId,
            @RequestParam LocalDate date) {
        QuizResponseDTO.QuestionDTO questionDTO = scheduleCommandService.deleteAttendanceQuiz(studyId, scheduleId, date);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_QUIZ_DELETED, questionDTO);
    }

    @Tag(name = "스터디 출석체크")
    @Operation(summary = "[스터디 출석체크] 회원 출석부 불러오기", description = """ 
            ## [스터디 출석체크] 지정된 날짜의 모든 스터디 회원의 출석 여부를 불러옵니다.
            * 출석체크 화면에 표시되는 스터디 회원 정보(프로필 사진, 이름, 출석 여부, 스터디장 여부) 목록를 반환합니다.
            * date에는 출석 정보를 확인할 날짜를 입력합니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "scheduleId", description = "출석을 확인할 일정의 id를 입력합니다.", required = true)
    @GetMapping("/studies/{studyId}/schedules/{scheduleId}/attendance")
    public ApiResponse<QuizResponseDTO.AttendanceListDTO> getAllAttendances(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistSchedule Long scheduleId,
            @RequestParam LocalDate date) {
        QuizResponseDTO.AttendanceListDTO attendanceListDTO = scheduleQueryService.getAllAttendances(studyId, scheduleId, date);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_MEMBER_ATTENDANCES_FOUND, attendanceListDTO);
    }
}