package com.example.spot.schedule.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.schedule.domain.Schedule;
import com.example.spot.schedule.domain.ScheduleRepository;
import com.example.spot.schedule.domain.association.Quiz;
import com.example.spot.schedule.domain.association.QuizSubmission;
import com.example.spot.schedule.domain.enums.SchedulePeriod;
import com.example.spot.schedule.domain.repository.QuizRepository;
import com.example.spot.schedule.domain.repository.QuizSubmissionRepository;
import com.example.spot.schedule.presentation.dto.response.QuizResponseDTO;
import com.example.spot.schedule.presentation.dto.response.ScheduleResponseDTO;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleQueryServiceImpl implements ScheduleQueryService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final ScheduleRepository scheduleRepository;
    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    /* ----------------------------- 스터디 일정 관련 API ------------------------------------- */

    /**
     * 특정 연/월의 일정을 불러오는 메서드입니다.
     *
     * @param studyId 일정을 불러올 스터디의 아이디를 입력 받습니다.
     * @param year    일정을 불러올 기준 연도를 입력 받습니다.
     * @param month   일정을 불러올 달을 입력 받습니다.
     * @return 스터디 아이디와 해당 스터디의 월별 일정 목록을 반환합니다.
     */
    @Override
    public ScheduleResponseDTO.MonthlyScheduleListDTO getMonthlySchedules(Long studyId, int year, int month) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        boolean isStudyMember;
        if (studyMemberRepository.existsByMemberIdAndStudyIdAndStatus(memberId, studyId,
                StudyApplicationStatus.APPROVED)) {
            isStudyMember = true;
        } else {
            isStudyMember = false;
        }

        List<ScheduleResponseDTO.MonthlyScheduleDTO> monthlyScheduleDTOS = new ArrayList<>();

        study.getSchedules().forEach(schedule -> {
            if (schedule.getSchedulePeriod().equals(SchedulePeriod.NONE)) {
                addSchedule(schedule, year, month, monthlyScheduleDTOS, isStudyMember);
            } else {
                addPeriodSchedules(schedule, year, month, monthlyScheduleDTOS, isStudyMember);
            }
        });

        return ScheduleResponseDTO.MonthlyScheduleListDTO.toDTO(study, monthlyScheduleDTOS);
    }

    /**
     * 하나의 일정에 대한 상세 정보를 불러오는 메서드입니다.
     *
     * @param studyId    일정을 불러올 스터디의 아이디를 입력 받습니다.
     * @param scheduleId 상세 정보를 물러올 일정의 아이디를 입력 받습니다.
     * @return 일정 아이디, 제목, 위치, 시작 일시, 종료 일시, 매일 진행 여부, 주기를 반환합니다.
     */
    @Override
    public ScheduleResponseDTO.MonthlyScheduleDTO getSchedule(Long studyId, Long scheduleId) {

        // Exception
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        boolean isStudyMember;
        if (studyMemberRepository.existsByMemberIdAndStudyIdAndStatus(memberId, studyId,
                StudyApplicationStatus.APPROVED)) {
            isStudyMember = true;
        } else {
            isStudyMember = false;
        }

        // 해당 스터디의 일정인지 확인
        scheduleRepository.findByIdAndStudyId(scheduleId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND));

        return ScheduleResponseDTO.MonthlyScheduleDTO.toDTO(schedule, isStudyMember);
    }

    /**
     * 로그인한 회원이 참여하는 특정 스터디의 다가오는 모임 목록을 페이징 조회 합니다.
     *
     * @param studyId  스터디 ID
     * @param pageable 페이징 정보
     * @return 다가오는 모임 목록을 반환합니다.
     * @throws GeneralException 스터디 일정이 존재하지 않는 경우
     * @throws GeneralException 스터디 멤버가 아닌 경우
     */
    @Override
    public ScheduleResponseDTO.SchedulePageDTO findStudySchedule(Long studyId, Pageable pageable) {

        // 로그인한 회원이 해당 스터디 회원인지 확인
        if (!isMember(SecurityUtils.getCurrentUserId(), studyId)) {
            throw new GeneralException(ErrorStatus._ONLY_STUDY_MEMBER_CAN_ACCESS_SCHEDULE);
        }

        // 스터디 일정 조회
        List<Schedule> schedules = scheduleRepository.findAllByStudyId(studyId, pageable);

        // 스터디 일정이 존재하지 않는 경우
        if (schedules.isEmpty()) {
            throw new GeneralException(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND);
        }

        // DTO로 변환하여 반환
        List<ScheduleResponseDTO.SchedulePreviewDTO> scheduleDTOS = schedules.stream()
                .map(schedule -> ScheduleResponseDTO.SchedulePreviewDTO.builder()
                        .title(schedule.getTitle())
                        .location(schedule.getLocation())
                        .startedAt(schedule.getStartedAt())
                        .finishedAt(schedule.getFinishedAt())
                        .build()).toList();

        // 페이징 처리
        return new ScheduleResponseDTO.SchedulePageDTO(new PageImpl<>(scheduleDTOS, pageable, schedules.size()),
                scheduleDTOS, schedules.size());
    }

    /**
     * 회원이 스터디 구성원인지 확인합니다.
     *
     * @param memberId 확인 하려는 회원 ID
     * @param studyId  확인 하려는 스터디 ID
     * @return 스터디 참여 여부를 반환합니다.
     */
    private boolean isMember(Long memberId, Long studyId) {
        return studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                StudyApplicationStatus.APPROVED).isPresent();
    }

    /**
     * 월별 일정 리스트에 주기가 정해져 있지 않은 일정을 추가하기 위한 메서드입니다. 일정의 시작일이 기준 연월과 일치하는 경우 월별 일정 리스트에 추가합니다. getMonthlySchedules API에서
     * 호출되는 내부 메서드입니다.
     *
     * @param schedule            리스트에 추가할 일정 정보를 입력 받습니다.
     * @param year                기준 연도를 입력 받습니다.
     * @param month               기준 월을 입력 받습니다.
     * @param monthlyScheduleDTOS 일정을 추가할 월별 일정 리스트를 입력 받습니다.
     * @param isStudyMember       스터디 회원 여부를 입력 받습니다.
     */
    private void addSchedule(Schedule schedule, int year, int month,
                             List<ScheduleResponseDTO.MonthlyScheduleDTO> monthlyScheduleDTOS, boolean isStudyMember) {
        if (schedule.getStartedAt().getYear() == year && schedule.getStartedAt().getMonthValue() == month) {
            monthlyScheduleDTOS.add(ScheduleResponseDTO.MonthlyScheduleDTO.toDTO(schedule, isStudyMember));
        }
    }

    /**
     * 월별 일정 리스트에 반복적인 일정을 추가하기 위한 메서드입니다. 일정의 시작일이 기준 연월 내에 있는 경우에만 일정을 추가하며, 주기에 따라 하나의 일정이라도 여러 번 추가될 수 있습니다. 예를 들어
     * 기준 연월이 2024년 8월이고, 2024년 8월 2일부터 시작되는 WEEKLY 일정이 있다고 가정 1. 이 일정은 기준 연월 내에서 2024년 8월 2일, 8월 9일, 8월 16일, 8월 23일, 8월
     * 30일에 시행 2. 따라서 monthlyScheduleDTOS에 추가되는 일정은 총 5개
     *
     * @param schedule            리스트에 추가할 일정 정보를 입력 받습니다.
     * @param year                기준 연도를 입력 받습니다.
     * @param month               기준 월을 입력 받습니다.
     * @param monthlyScheduleDTOS 일정을 추가할 월별 일정 리스트를 입력 받습니다.
     * @param isStudyMember       스터디 회원 여부를 입력 받습니다.
     */
    private void addPeriodSchedules(Schedule schedule, int year, int month,
                                    List<ScheduleResponseDTO.MonthlyScheduleDTO> monthlyScheduleDTOS,
                                    boolean isStudyMember) {

        LocalDateTime startedAt = schedule.getStartedAt();
        LocalDateTime finishedAt = schedule.getFinishedAt();

        YearMonth yearMonth = YearMonth.of(year, month); // 탐색 연월
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59); // 탐색 연월의 마지막 날

        // 일정 시작일이 탐색 연월 내에 있는 경우만 반복
        while (startedAt.isBefore(endOfMonth)) {
            // 업데이트된 일정 시작일의 month가 탐색 month와 일치하면 추가
            if (startedAt.getMonthValue() == month) {
                monthlyScheduleDTOS.add(
                        ScheduleResponseDTO.MonthlyScheduleDTO.toDTOWithDate(schedule, startedAt, finishedAt,
                                isStudyMember));
            }

            if (schedule.getSchedulePeriod().equals(SchedulePeriod.DAILY)) {
                startedAt = startedAt.plusDays(1);
                finishedAt = finishedAt.plusDays(1);
            } else if (schedule.getSchedulePeriod().equals(SchedulePeriod.WEEKLY)) {
                startedAt = startedAt.plusWeeks(1);
                finishedAt = finishedAt.plusWeeks(1);
            } else if (schedule.getSchedulePeriod().equals(SchedulePeriod.BIWEEKLY)) {
                startedAt = startedAt.plusWeeks(2);
                finishedAt = finishedAt.plusWeeks(2);
            } else if (schedule.getSchedulePeriod().equals(SchedulePeriod.MONTHLY)) {
                startedAt = startedAt.plusMonths(1);
                finishedAt = finishedAt.plusMonths(1);
            }
        }
    }

    /* ----------------------------- 스터디 출석 관련 API ------------------------------------- */

    /**
     * 특정 일자에 대한 모든 스터디 회원의 출석 정보를 불러옵니다.
     *
     * @param studyId    출석 정보를 불러올 스터디의 아이디를 입력 받습니다.
     * @param scheduleId 스터디 일정의 아이디를 입력 받습니다.
     * @param date       출석 정보를 불러올 날짜를 입력 받습니다.
     * @return 모든 스터디 회원에 대한 정보와 출석 여부를 담은 리스트를 반환합니다.
     */
    @Override
    public QuizResponseDTO.AttendanceListDTO getAllAttendances(Long studyId, Long scheduleId, LocalDate date) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 요청한 날짜에 생성된 출석 퀴즈 조회
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atStartOfDay().plusDays(1);
        List<Quiz> todayQuizzes = quizRepository.findAllByScheduleIdAndCreatedAtBetween(scheduleId, startOfDay,
                endOfDay);
        if (todayQuizzes.isEmpty()) {
            throw new StudyHandler(ErrorStatus._STUDY_QUIZ_NOT_FOUND);
        }
        Quiz quiz = todayQuizzes.get(0);

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        //=== Feature ===//
        List<QuizResponseDTO.AttendingMemberDTO> studyMembers = studyMemberRepository.findAllByStudyIdAndStatus(studyId,
                        StudyApplicationStatus.APPROVED).stream()
                .map(memberStudy -> {
                    List<QuizSubmission> attendanceList = quizSubmissionRepository.findByQuizIdAndMemberId(quiz.getId(),
                            memberStudy.getMember().getId());
                    for (QuizSubmission attendance : attendanceList) {
                        // MemberAttendance에 퀴즈에 대한 정답이 저장되어 있으면 금일 출석 성공
                        if (attendance.getIsCorrect()) {
                            return QuizResponseDTO.AttendingMemberDTO.toDTO(memberStudy, Boolean.TRUE);
                        }
                    }
                    // 퀴즈를 풀지 않았거나 MemberAttendance에 오답만 저장되어 있으면 금일 출석 실패
                    return QuizResponseDTO.AttendingMemberDTO.toDTO(memberStudy, Boolean.FALSE);
                })
                .toList();

        return QuizResponseDTO.AttendanceListDTO.toDTO(quiz, studyMembers);

    }

    @Override
    public QuizResponseDTO.QuestionDTO getAttendanceQuiz(Long studyId, Long scheduleId, LocalDate date) {

        // Authorization
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND));

        // 해당 스터디에서 생성된 일정인지 확인
        if (!schedule.getStudy().equals(study)) {
            throw new StudyHandler(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND);
        }

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 날짜에 생성된 스터디 퀴즈 조회
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atStartOfDay().plusDays(1);
        List<Quiz> todayQuizzes = quizRepository.findAllByScheduleIdAndCreatedAtBetween(scheduleId, startOfDay,
                endOfDay);
        if (todayQuizzes.isEmpty()) {
            throw new StudyHandler(ErrorStatus._STUDY_QUIZ_NOT_FOUND);
        }
        Quiz quiz = todayQuizzes.get(0);

        return QuizResponseDTO.QuestionDTO.toDTO(quiz);
    }


}
