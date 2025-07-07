package com.example.spot.schedule.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.MemberRepository;
import com.example.spot.notification.domain.Notification;
import com.example.spot.notification.domain.NotificationRepository;
import com.example.spot.notification.domain.enums.NotifyType;
import com.example.spot.schedule.domain.Schedule;
import com.example.spot.schedule.domain.ScheduleRepository;
import com.example.spot.schedule.domain.association.Quiz;
import com.example.spot.schedule.domain.association.QuizSubmission;
import com.example.spot.schedule.domain.repository.QuizRepository;
import com.example.spot.schedule.domain.repository.QuizSubmissionRepository;
import com.example.spot.schedule.presentation.dto.request.QuizRequestDTO;
import com.example.spot.schedule.presentation.dto.request.ScheduleRequestDTO;
import com.example.spot.schedule.presentation.dto.response.QuizResponseDTO;
import com.example.spot.schedule.presentation.dto.response.ScheduleResponseDTO;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleCommandServiceImpl implements ScheduleCommandService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final NotificationRepository notificationRepository;
    private final ScheduleRepository scheduleRepository;
    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    /* ----------------------------- 스터디 일정 관련 API ------------------------------------- */

    /**
     * 스터디 일정을 추가하는 메서드입니다.
     *
     * @param studyId            타겟 스터디의 아이디를 입력 받습니다.
     * @param scheduleRequestDTO 생성할 일정의 제목, 위치, 시작 일시, 종료 일시, 종일 진행 여부, 반복 여부를 입력 받습니다.
     * @return 스터디 아이디와 생성된 일정의 아이디, 제목을 반환합니다.
     */
    @Override
    public ScheduleResponseDTO.ScheduleDTO addSchedule(Long studyId,
                                                       ScheduleRequestDTO.ScheduleDTO scheduleRequestDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        //=== Feature ===//

        // Period 기반 시작일 종료일 제한
        checkStartAndFinishDate(scheduleRequestDTO);

        Schedule schedule = Schedule.builder()
                .study(study)
                .member(member)
                .title(scheduleRequestDTO.getTitle())
                .location(scheduleRequestDTO.getLocation())
                .startedAt(scheduleRequestDTO.getStartedAt())
                .finishedAt(scheduleRequestDTO.getFinishedAt())
                .isAllDay(scheduleRequestDTO.getIsAllDay())
                .schedulePeriod(scheduleRequestDTO.getSchedulePeriod())
                .build();

        // 알림 생성

        // 스터디에 참여중인 회원들에게 알림 전송 위해 회원 조회
        List<Member> members = studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED)
                .stream()
                .map(StudyMember::getMember)
                .toList();

        if (members.isEmpty()) {
            throw new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND);
        }

        members.forEach(studyMember -> {
            Notification notification = Notification.builder()
                    .member(studyMember)
                    .study(study)
                    .notifierName(member.getName()) // 일정 생성자 이름
                    .type(NotifyType.SCHEDULE_UPDATE)
                    .isChecked(Boolean.FALSE)
                    .build();
            notificationRepository.save(notification);
        });

        scheduleRepository.save(schedule);
        study.addSchedule(schedule);

        return ScheduleResponseDTO.ScheduleDTO.toDTO(schedule);
    }

    /**
     * 스터디 일정을 변경하는 메서드입니다.
     *
     * @param studyId        타겟 스터디의 아이디를 입력 받습니다.
     * @param scheduleId     변경할 일정의 아이디를 입력 받습니다.
     * @param scheduleModDTO 변경된 일정의 제목, 위치, 시작 일시, 종료 일시, 종일 진행 여부, 반복 여부를 입력 받습니다.
     * @return 스터디 아이디와 변경된 일정의 아이디, 제목을 반환합니다.
     */
    @Override
    public ScheduleResponseDTO.ScheduleDTO modSchedule(Long studyId, Long scheduleId,
                                                       ScheduleRequestDTO.ScheduleDTO scheduleModDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 로그인한 회원이 일정 생성자인지 확인
        scheduleRepository.findByIdAndMemberId(scheduleId, memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._SCHEDULE_MOD_INVALID));

        // 해당 스터디의 일정인지 확인
        scheduleRepository.findByIdAndStudyId(scheduleId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND));

        //=== Feature ===//

        // Period 기반 시작일 종료일 제한
        checkStartAndFinishDate(scheduleModDTO);

        schedule.modSchedule(scheduleModDTO);
        schedule = scheduleRepository.save(schedule);

        study.updateSchedule(schedule);

        return ScheduleResponseDTO.ScheduleDTO.toDTO(schedule);
    }

    private static void checkStartAndFinishDate(ScheduleRequestDTO.ScheduleDTO scheduleRequestDTO) {
        LocalDate startDate = scheduleRequestDTO.getStartedAt().toLocalDate();
        LocalDate finishDate = scheduleRequestDTO.getFinishedAt().toLocalDate();
        System.out.println(startDate);
        System.out.println(finishDate);
        switch (scheduleRequestDTO.getSchedulePeriod()) {
            case DAILY:
                // 시작일과 종료일이 일치해야 함
                if (finishDate.equals(startDate.plusDays(1)) ||
                        finishDate.isAfter(startDate.plusDays(1))) {
                    throw new StudyHandler(ErrorStatus._STUDY_SCHEDULE_WRONG_FORMAT);
                }
            case WEEKLY:
                // 시작일과 종료일이 일주일 이상 차이나지 않아야 함
                if (finishDate.equals(startDate.plusWeeks(1)) ||
                        finishDate.isAfter(startDate.plusWeeks(1))) {
                    throw new StudyHandler(ErrorStatus._STUDY_SCHEDULE_WRONG_FORMAT);
                }
            case BIWEEKLY:
                // 시작일과 종료일이 2주 이상 차이나지 않아야 함
                if (finishDate.equals(startDate.plusWeeks(2)) ||
                        finishDate.isAfter(startDate.plusWeeks(2))) {
                    throw new StudyHandler(ErrorStatus._STUDY_SCHEDULE_WRONG_FORMAT);
                }
            case MONTHLY:
                // 시작일과 종료일이 한 달 이상 차이나지 않아야 함
                if (finishDate.equals(startDate.plusMonths(1)) ||
                        finishDate.isAfter(startDate.plusMonths(1))) {
                    throw new StudyHandler(ErrorStatus._STUDY_SCHEDULE_WRONG_FORMAT);
                }
        }
    }


    /* ----------------------------- 스터디 출석 관련 API ------------------------------------- */

    /**
     * 출석 퀴즈를 생성하는 메서드입니다.
     *
     * @param studyId        타겟 스터디의 아이디를 입력 받습니다.
     * @param scheduleId     타겟 일정의 아이디를 입력 받습니다.
     * @param quizRequestDTO 출석 퀴즈에 담길 질문과 정답을 입력 받습니다.
     * @return 생성된 퀴즈의 아이디와 질문이 반환됩니다.
     */
    @Override
    public QuizResponseDTO.QuestionDTO createAttendanceQuiz(Long studyId, Long scheduleId,
                                                            QuizRequestDTO.QuizDTO quizRequestDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 해당 스터디에서 생성된 일정인지 확인
        if (!schedule.getStudy().equals(study)) {
            throw new StudyHandler(ErrorStatus._STUDY_SCHEDULE_NOT_FOUND);
        }

        // 로그인한 회원이 스터디장인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(memberId, studyId, Boolean.TRUE)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_QUIZ_CREATION_INVALID));

        // 요청한 날짜에 이미 출석 퀴즈가 생성되었는지 확인
        LocalDateTime startOfDay = quizRequestDTO.getCreatedAt().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = quizRequestDTO.getCreatedAt().withHour(23).withMinute(59).withSecond(59)
                .withNano(999_999_000);
        List<Quiz> todayQuizzes = quizRepository.findAllByScheduleIdAndCreatedAtBetween(scheduleId, startOfDay,
                endOfDay);
        if (!todayQuizzes.isEmpty()) {
            throw new StudyHandler(ErrorStatus._STUDY_QUIZ_ALREADY_EXIST);
        }

        //=== Feature ===//
        Quiz quiz = Quiz.builder()
                .schedule(schedule)
                .member(member)
                .question(quizRequestDTO.getQuestion())
                .answer(quizRequestDTO.getAnswer())
                .createdAt(quizRequestDTO.getCreatedAt())
                .build();

        quiz = quizRepository.save(quiz);
        schedule.addQuiz(quiz);

        return QuizResponseDTO.QuestionDTO.toDTO(quiz);
    }

    /**
     * 출석 체크에 사용되는 메서드입니다. 메서드 내에서 퀴즈의 제한 시간과 시도 횟수를 확인하며, 조건을 충족하는 경우 회원 출석 정보를 저장합니다.
     *
     * @param studyId              타겟 스터디의 아이디를 입력 받습니다.
     * @param scheduleId           출석을 체크할 일정을 입력 받습니다.
     * @param attendanceRequestDTO 퀴즈에 대한 회원의 답변을 입력 받습니다.
     * @return 회원 아이디, 퀴즈 아이디, 출석 아이디, 정답 여부, 시도 횟수, 출석 정보 생성 시각을 반환합니다.
     */
    @Override
    public QuizResponseDTO.AttendanceDTO attendantStudy(Long studyId, Long scheduleId,
                                                        QuizRequestDTO.AttendanceDTO attendanceRequestDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 요청한 날짜에 생성된 출석 퀴즈 조회
        LocalDateTime startOfDay = attendanceRequestDTO.getDateTime().withHour(0).withMinute(0).withSecond(0)
                .withNano(0);
        LocalDateTime endOfDay = attendanceRequestDTO.getDateTime().withHour(23).withMinute(59).withSecond(59)
                .withNano(999_999_000);
        List<Quiz> quizzes = quizRepository.findAllByScheduleIdAndCreatedAtBetween(scheduleId, startOfDay, endOfDay);
        if (quizzes.isEmpty()) {
            throw new StudyHandler(ErrorStatus._STUDY_QUIZ_NOT_FOUND);
        }
        Quiz quiz = quizzes.get(0);

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), study.getId(),
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 퀴즈 제한시간 확인
        if (attendanceRequestDTO.getDateTime().isAfter(quiz.getCreatedAt().plusMinutes(5))) {
            throw new StudyHandler(ErrorStatus._STUDY_QUIZ_NOT_VALID);
        }

        // 이미 출석이 완료되었거나 시도 횟수를 초과하였는지 확인
        List<QuizSubmission> attendanceList = quizSubmissionRepository.findByQuizIdAndMemberId(quiz.getId(),
                member.getId());
        int try_num = 0;
        for (QuizSubmission attendance : attendanceList) {
            if (attendance.getIsCorrect()) {
                throw new StudyHandler(ErrorStatus._STUDY_ATTENDANCE_ALREADY_EXIST);
            } else {
                try_num++;
            }
        }
        if (try_num >= 3) {
            throw new StudyHandler(ErrorStatus._STUDY_ATTENDANCE_ATTEMPT_LIMIT_EXCEEDED);
        }

        //=== Feature ===//
        Boolean isCorrect;
        if (attendanceRequestDTO.getAnswer().equals(quiz.getAnswer())) {
            isCorrect = Boolean.TRUE;
        } else {
            isCorrect = Boolean.FALSE;
        }

        QuizSubmission quizSubmission = new QuizSubmission(isCorrect);
        quiz.addMemberAttendance(quizSubmission);
        quizSubmission = quizSubmissionRepository.save(quizSubmission);

        return QuizResponseDTO.AttendanceDTO.toDTO(quizSubmission, try_num + 1);
    }

    /**
     * 출석 퀴즈를 삭제하는 메서드입니다.
     *
     * @param studyId    타겟 스터디의 아이디를 입력 받습니다.
     * @param scheduleId 스터디 일정의 아이디를 입력 받습니다.
     * @param date       출석 퀴즈가 생성된 날짜를 입력 받습니다.
     * @return 삭제된 퀴즈의 아이디와 질문을 반환합니다.
     */
    @Override
    public QuizResponseDTO.QuestionDTO deleteAttendanceQuiz(Long studyId, Long scheduleId, LocalDate date) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
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
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), study.getId(),
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 로그인한 회원이 스터디장인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(memberId, studyId, Boolean.TRUE)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_QUIZ_DELETION_INVALID));

        //=== Feature ===//
        quizSubmissionRepository.findByQuizId(quiz.getId())
                .forEach(memberAttendance -> {
                    quiz.deleteMemberAttendance(memberAttendance);
                    quizSubmissionRepository.delete(memberAttendance);
                });
        quizRepository.delete(quiz);

        return QuizResponseDTO.QuestionDTO.toDTO(quiz);
    }


}
