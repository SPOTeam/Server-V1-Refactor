package com.example.spot.study.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.report.domain.MemberReport;
import com.example.spot.notification.domain.Notification;
import com.example.spot.schedule.domain.Schedule;
import com.example.spot.schedule.domain.association.Quiz;
import com.example.spot.schedule.domain.association.QuizSubmission;
import com.example.spot.schedule.domain.repository.QuizRepository;
import com.example.spot.schedule.domain.repository.QuizSubmissionRepository;
import com.example.spot.schedule.domain.ScheduleRepository;
import com.example.spot.story.domain.Story;
import com.example.spot.story.domain.association.StoryReport;
import com.example.spot.todo.domain.ToDo;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.notification.domain.enums.NotifyType;
import com.example.spot.member.domain.enums.Status;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.Study;
import com.example.spot.report.domain.MemberReportRepository;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.notification.domain.NotificationRepository;
import com.example.spot.story.domain.repository.StoryReportRepository;
import com.example.spot.story.domain.StoryRepository;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.todo.domain.ToDoRepository;
import com.example.spot.schedule.presentation.dto.request.ScheduleRequestDTO;
import com.example.spot.study.presentation.dto.request.StudyHostWithdrawRequestDTO;
import com.example.spot.study.presentation.dto.request.StudyMemberReportDTO;
import com.example.spot.schedule.presentation.dto.request.StudyQuizRequestDTO;
import com.example.spot.study.presentation.dto.request.StudyVoteRequestDTO;
import com.example.spot.schedule.presentation.dto.response.ScheduleResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyPostResDTO;
import com.example.spot.schedule.presentation.dto.response.StudyQuizResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyTerminationResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyVoteResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyWithdrawalResponseDTO;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.common.application.s3.S3ImageService;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.study.presentation.dto.request.ToDoListRequestDTO.ToDoListCreateDTO;
import com.example.spot.study.presentation.dto.response.ToDoListResponseDTO.ToDoListCreateResponseDTO;
import com.example.spot.study.presentation.dto.response.ToDoListResponseDTO.ToDoListUpdateResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyWithdrawalResponseDTO.WithdrawalDTO;
import com.example.spot.study.presentation.dto.response.StudyApplyResponseDTO;

import java.time.LocalDate;
import java.util.Objects;

import com.example.spot.vote.domain.Vote;
import com.example.spot.vote.domain.association.VoteOption;
import com.example.spot.vote.domain.association.VoteParticipant;
import com.example.spot.vote.domain.repository.VoteOptionRepository;
import com.example.spot.vote.domain.repository.VoteParticipantRepository;
import com.example.spot.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class StudyMemberCommandServiceImpl implements StudyMemberCommandService {

    @Value("${image.post.anonymous.profile}")
    private String defaultImage;

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final ScheduleRepository scheduleRepository;
    private final QuizRepository quizRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final MemberReportRepository memberReportRepository;
    private final StoryReportRepository storyReportRepository;

    private final StudyMemberRepository studyMemberRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final StoryRepository storyRepository;
    private final VoteParticipantRepository voteParticipantRepository;
    private final ToDoRepository toDoRepository;
    private final NotificationRepository notificationRepository;

    // S3 Service
    private final S3ImageService s3ImageService;

/* ----------------------------- 진행중인 스터디 관련 API ------------------------------------- */

    /**
     * 진행중인 스터디에서 탈퇴하기 위한 메서드입니다.
     * 스터디장은 스터디를 탈퇴할 수 없으며 스터디를 종료하고자 하는 경우 스터디 terminateStudy API를 호출해야 합니다.
     *
     * @param studyId 타겟 회원이 탈퇴하고자 하는 스터디의 아이디를 입력 받습니다.
     * @return 탈퇴한 스터디의 아이디와 이름, 탈퇴한 회원의 아이디와 이름이 반환됩니다.
     */
    public StudyWithdrawalResponseDTO.WithdrawalDTO withdrawFromStudy(Long studyId) {

        // Authorization
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 참여가 승인되지 않은 스터디는 탈퇴할 수 없음
        if (!studyMember.getStatus().equals(StudyApplicationStatus.APPROVED)) {
            throw new StudyHandler(ErrorStatus._STUDY_NOT_APPROVED);
        }
        // 스터디장은 스터디를 탈퇴할 수 없음
        if (studyMember.getIsOwned()) {
            throw new StudyHandler(ErrorStatus._STUDY_OWNER_CANNOT_WITHDRAW);
        }

        studyMemberRepository.delete(studyMember);

        return StudyWithdrawalResponseDTO.WithdrawalDTO.toDTO(member, study);
    }

    @Override
    public WithdrawalDTO withdrawHostFromStudy(Long studyId, StudyHostWithdrawRequestDTO requestDTO) {
        // Authorization
        Long hostId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(hostId);

        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(hostId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        if (!studyMember.getIsOwned()) {
            throw new StudyHandler(ErrorStatus._STUDY_OWNER_ONLY_CAN_WITHDRAW);
        }

        StudyMember newHostStudy = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(requestDTO.getNewHostId(), studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_EXIST));

        studyMemberRepository.delete(studyMember);

        newHostStudy.setIsOwned(true);
        newHostStudy.setReason(requestDTO.getReason());

        studyMemberRepository.save(newHostStudy);

        return StudyWithdrawalResponseDTO.WithdrawalDTO.toDTO(newHostStudy.getMember(), newHostStudy.getStudy());
    }
    /**
     * 운영중인 스터디를 종료하는 메서드입니다. 스터디장만 호출 가능합니다.
     *
     * @param studyId       종료할 스터디의 아이디를 입력 받습니다.
     * @param performance   종료할 스터디의 성과를 입력 받습니다.
     * @return 종료된 스터디의 아이디, 이름, 상태를 반환합니다.
     */
    public StudyTerminationResponseDTO.TerminationDTO terminateStudy(Long studyId, String performance) {

        // Authorization
        Long memberId = SecurityUtils.getCurrentUserId();
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 스터디장이 아니면 스터디를 종료할 수 없음
        if (studyMember.getIsOwned().equals(false)) {
            throw new StudyHandler(ErrorStatus._STUDY_OWNER_ONLY_CAN_TERMINATE);
        }

        // 이미 종료된 스터디는 종료할 수 없음
        if (study.getStatus().equals(Status.OFF)) {
            throw new StudyHandler(ErrorStatus._STUDY_ALREADY_TERMINATED);
        }

        study.terminateStudy(performance);
        studyRepository.save(study);

        return StudyTerminationResponseDTO.TerminationDTO.toDTO(study);
    }

    /**
     * 스터디 신청을 처리합니다. isAccept가 true이면 승인, false이면 거절합니다.
     * 이후 관련 알림을 생성합니다. 알림을 통해 최종 참여 승인을 해야 스터디에 참여할 수 있습니다.
     * @param memberId 스터디에 신청한 회원 ID
     * @param studyId 스터디 ID
     * @param isAccept 승인 여부
     * @return 스터디 신청 처리 결과 및 처리 시간
     * @throws GeneralException 스터디 신청을 처리하는 회원이 스터디 소유자가 아닐 때
     * @throws GeneralException 스터디 소유자가 신청한 경우
     * @throws StudyHandler 스터디 신청자를 찾을 수 없을 때
     * @throws StudyHandler 스터디 신청이 이미 처리되었을 때
     * @throws MemberHandler 스터디 장을 찾을 수 없을 때
     */
    @Override
    public StudyApplyResponseDTO acceptAndRejectStudyApply(Long memberId, Long studyId,
        boolean isAccept) {

        // 신청을 처리하는 회원이 스터디 소유자인지 확인
        if (!isOwner(SecurityUtils.getCurrentUserId(), studyId))
            throw new GeneralException(ErrorStatus._ONLY_STUDY_OWNER_CAN_ACCESS_APPLICANTS);

        // 스터디 신청자 조회
        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPLIED)
            .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_APPLICANT_NOT_FOUND));

        // 스터디 소유자가 스터디 신청한 경우
        if (studyMember.getIsOwned())
            throw new GeneralException(ErrorStatus._STUDY_OWNER_CANNOT_APPLY);

        // 스터디 신청이 이미 처리되었을 때
        if (studyMember.getStatus() != StudyApplicationStatus.APPLIED)
            throw new GeneralException(ErrorStatus._STUDY_APPLY_ALREADY_PROCESSED);

        // 스터디 장 조회
        Member owner = memberRepository.findById(SecurityUtils.getCurrentUserId())
            .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 승인인 경우
        if (isAccept) {
            // 스터디 참여 승인 최종 대기
            studyMember.setStatus(StudyApplicationStatus.AWAITING_SELF_APPROVAL);

            // 알림 생성
            Notification notification = Notification.builder()
                .member(studyMember.getMember()) // 신청자
                .study(studyMember.getStudy())
                .notifierName(owner.getName()) // 스터디장 이름
                .type(NotifyType.STUDY_APPLY)
                .isChecked(Boolean.FALSE)
                .build();

            notificationRepository.save(notification);
        }
        else { // 거절인 경우
            studyMember.setStatus(StudyApplicationStatus.REJECTED);
            studyMemberRepository.delete(studyMember);
        }

        // 스터디 신청 처리 결과 반환
        return StudyApplyResponseDTO.builder()
            .status(studyMember.getStatus())
            .updatedAt(studyMember.getUpdatedAt())
            .build();
    }

    /**
     * 스터디 신청을 처리합니다. isAccept가 true이면 승인, false이면 거절합니다.
     * 이 메서드를 사용하면 알림 처리 없이 바로 스터디에 참여할 수 있습니다.
     * @param memberId 스터디에 신청한 회원 ID
     * @param studyId 스터디 ID
     * @param isAccept 승인 여부
     * @return 스터디 신청 처리 결과 및 처리 시간
     * @throws GeneralException 스터디 신청을 처리하는 회원이 스터디 소유자가 아닐 때
     * @throws GeneralException 스터디 소유자가 신청한 경우
     * @throws StudyHandler 스터디 신청자를 찾을 수 없을 때
     * @throws StudyHandler 스터디 신청이 이미 처리되었을 때
     * @throws MemberHandler 스터디 장을 찾을 수 없을 때
     *
     */
    @Override
    public StudyApplyResponseDTO acceptAndRejectStudyApplyForTest(Long memberId, Long studyId,
        boolean isAccept) {

        // 스터디 신청을 처리하는 회원이 스터디 소유자인지 확인
        if (!isOwner(SecurityUtils.getCurrentUserId(), studyId))
            throw new GeneralException(ErrorStatus._ONLY_STUDY_OWNER_CAN_ACCESS_APPLICANTS);

        // 스터디 신청자 조회
        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPLIED)
            .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_APPLICANT_NOT_FOUND));

        // 스터디 소유자가 스터디 신청한 경우
        if (studyMember.getIsOwned())
            throw new GeneralException(ErrorStatus._STUDY_OWNER_CANNOT_APPLY);

        // 스터디 신청이 이미 처리되었을 때
        if (studyMember.getStatus() != StudyApplicationStatus.APPLIED)
            throw new GeneralException(ErrorStatus._STUDY_APPLY_ALREADY_PROCESSED);

        // 스터디 장 조회
        Member owner = memberRepository.findById(SecurityUtils.getCurrentUserId())
            .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 승인인 경우
        if (isAccept) {
            studyMember.setStatus(StudyApplicationStatus.APPROVED);
        }
        else {
            studyMember.setStatus(StudyApplicationStatus.REJECTED);
            studyMemberRepository.delete(studyMember);
        }

        // 스터디 신청 처리 결과 반환
        return StudyApplyResponseDTO.builder()
            .status(studyMember.getStatus())
            .updatedAt(studyMember.getUpdatedAt())
            .build();
    }

    /* ----------------------------- 스터디 일정 관련 API ------------------------------------- */

    /**
     * 스터디 일정을 추가하는 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param scheduleRequestDTO 생성할 일정의 제목, 위치, 시작 일시, 종료 일시, 종일 진행 여부, 반복 여부를 입력 받습니다.
     * @return 스터디 아이디와 생성된 일정의 아이디, 제목을 반환합니다.
     */
    @Override
    public ScheduleResponseDTO.ScheduleDTO addSchedule(Long studyId, ScheduleRequestDTO.ScheduleDTO scheduleRequestDTO) {

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
        List<Member> members = studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED).stream()
            .map(StudyMember::getMember)
            .toList();

        if (members.isEmpty())
            throw new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND);

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
        member.addSchedule(schedule);

        return ScheduleResponseDTO.ScheduleDTO.toDTO(schedule);
    }

    /**
     * 스터디 일정을 변경하는 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param scheduleId 변경할 일정의 아이디를 입력 받습니다.
     * @param scheduleModDTO 변경된 일정의 제목, 위치, 시작 일시, 종료 일시, 종일 진행 여부, 반복 여부를 입력 받습니다.
     * @return 스터디 아이디와 변경된 일정의 아이디, 제목을 반환합니다.
     */
    @Override
    public ScheduleResponseDTO.ScheduleDTO modSchedule(Long studyId, Long scheduleId, ScheduleRequestDTO.ScheduleDTO scheduleModDTO) {

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
        member.updateSchedule(schedule);

        return ScheduleResponseDTO.ScheduleDTO.toDTO(schedule);
    }

    private static void checkStartAndFinishDate(ScheduleRequestDTO.ScheduleDTO scheduleRequestDTO) {
        LocalDate startDate = scheduleRequestDTO.getStartedAt().toLocalDate();
        LocalDate finishDate = scheduleRequestDTO.getFinishedAt().toLocalDate();
        System.out.println(startDate);
        System.out.println(finishDate);
        switch (scheduleRequestDTO.getSchedulePeriod()) {
            case DAILY :
                // 시작일과 종료일이 일치해야 함
                if (finishDate.equals(startDate.plusDays(1)) ||
                        finishDate.isAfter(startDate.plusDays(1))) {
                    throw new StudyHandler(ErrorStatus._STUDY_SCHEDULE_WRONG_FORMAT);
                }
            case WEEKLY :
                // 시작일과 종료일이 일주일 이상 차이나지 않아야 함
                if (finishDate.equals(startDate.plusWeeks(1)) ||
                        finishDate.isAfter(startDate.plusWeeks(1))) {
                    throw new StudyHandler(ErrorStatus._STUDY_SCHEDULE_WRONG_FORMAT);
                }
            case BIWEEKLY :
                // 시작일과 종료일이 2주 이상 차이나지 않아야 함
                if (finishDate.equals(startDate.plusWeeks(2)) ||
                        finishDate.isAfter(startDate.plusWeeks(2))) {
                    throw new StudyHandler(ErrorStatus._STUDY_SCHEDULE_WRONG_FORMAT);
                }
            case MONTHLY :
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
     * @param studyId        타겟 스터디의 아이디를 입력 받습니다.
     * @param scheduleId     타겟 일정의 아이디를 입력 받습니다.
     * @param quizRequestDTO 출석 퀴즈에 담길 질문과 정답을 입력 받습니다.
     * @return 생성된 퀴즈의 아이디와 질문이 반환됩니다.
     */
    @Override
    public StudyQuizResponseDTO.QuizDTO createAttendanceQuiz(Long studyId, Long scheduleId, StudyQuizRequestDTO.QuizDTO quizRequestDTO) {

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
        LocalDateTime endOfDay = quizRequestDTO.getCreatedAt().withHour(23).withMinute(59).withSecond(59).withNano(999_999_000);
        List<Quiz> todayQuizzes = quizRepository.findAllByScheduleIdAndCreatedAtBetween(scheduleId, startOfDay, endOfDay);
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
        member.addQuiz(quiz);

        return StudyQuizResponseDTO.QuizDTO.toDTO(quiz);
    }

    /**
     * 출석 체크에 사용되는 메서드입니다.
     * 메서드 내에서 퀴즈의 제한 시간과 시도 횟수를 확인하며, 조건을 충족하는 경우 회원 출석 정보를 저장합니다.
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param scheduleId 출석을 체크할 일정을 입력 받습니다.
     * @param attendanceRequestDTO 퀴즈에 대한 회원의 답변을 입력 받습니다.
     * @return 회원 아이디, 퀴즈 아이디, 출석 아이디, 정답 여부, 시도 횟수, 출석 정보 생성 시각을 반환합니다.
     */
    @Override
    public StudyQuizResponseDTO.AttendanceDTO attendantStudy(Long studyId, Long scheduleId, StudyQuizRequestDTO.AttendanceDTO attendanceRequestDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 요청한 날짜에 생성된 출석 퀴즈 조회
        LocalDateTime startOfDay = attendanceRequestDTO.getDateTime().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = attendanceRequestDTO.getDateTime().withHour(23).withMinute(59).withSecond(59).withNano(999_999_000);
        List<Quiz> quizzes = quizRepository.findAllByScheduleIdAndCreatedAtBetween(scheduleId, startOfDay, endOfDay);
        if (quizzes.isEmpty()) {
            throw new StudyHandler(ErrorStatus._STUDY_QUIZ_NOT_FOUND);
        }
        Quiz quiz = quizzes.get(0);

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), study.getId(), StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 퀴즈 제한시간 확인
        if (attendanceRequestDTO.getDateTime().isAfter(quiz.getCreatedAt().plusMinutes(5))) {
            throw new StudyHandler(ErrorStatus._STUDY_QUIZ_NOT_VALID);
        }

        // 이미 출석이 완료되었거나 시도 횟수를 초과하였는지 확인
        List<QuizSubmission> attendanceList = quizSubmissionRepository.findByQuizIdAndMemberId(quiz.getId(), member.getId());
        int try_num = 0;
        for (QuizSubmission attendance : attendanceList) {
            if (attendance.getIsCorrect())
                throw new StudyHandler(ErrorStatus._STUDY_ATTENDANCE_ALREADY_EXIST);
            else
                try_num++;
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
        member.addMemberAttendance(quizSubmission);
        quiz.addMemberAttendance(quizSubmission);
        quizSubmission = quizSubmissionRepository.save(quizSubmission);

        return StudyQuizResponseDTO.AttendanceDTO.toDTO(quizSubmission, try_num+1);
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
    public StudyQuizResponseDTO.QuizDTO deleteAttendanceQuiz(Long studyId, Long scheduleId, LocalDate date) {

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
        List<Quiz> todayQuizzes = quizRepository.findAllByScheduleIdAndCreatedAtBetween(scheduleId, startOfDay, endOfDay);
        if (todayQuizzes.isEmpty()) {
            throw new StudyHandler(ErrorStatus._STUDY_QUIZ_NOT_FOUND);
        }
        Quiz quiz = todayQuizzes.get(0);

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), study.getId(), StudyApplicationStatus.APPROVED)
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

        return StudyQuizResponseDTO.QuizDTO.toDTO(quiz);
    }


/* ----------------------------- 스터디 투표 관련 API ------------------------------------- */

    /**
     * 스터디 투표를 생성하는 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param voteDTO 생성할 투표의 제목, 항목 목록, 중복 선택 가능 여부, 종료 일시를 입력 받습니다.
     * @return 생성된 투표의 아이디와 제목을 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.VotePreviewDTO createVote(Long studyId, StudyVoteRequestDTO.VoteDTO voteDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member loginMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        //=== Feature ===//
        Vote vote = Vote.builder()
                .study(study)
                .member(loginMember)
                .title(voteDTO.getTitle())
                .isMultipleChoice(voteDTO.getIsMultipleChoice())
                .finishedAt(voteDTO.getFinishedAt())
                .build();

        // Vote 저장
        vote = voteRepository.save(vote);
        // Option 저장
        vote = createOption(vote, voteDTO);
        // 연관관계 매핑
        loginMember.addVote(vote);
        study.addVote(vote);

        return StudyVoteResponseDTO.VotePreviewDTO.toDTO(vote);
    }

    /**
     * 스터디 투표의 항목을 생성하는 메서드입니다.
     * createVote 메서드 내부에서 사용되는 메서드입니다.
     * @param vote 항목을 생성할 타겟 투표를 입력 받습니다.
     * @param voteDTO 생성할 투표의 제목, 항목 목록, 중복 선택 가능 여부, 종료 일시를 입력 받습니다.
     * @return 투표 객체를 반환합니다.
     */
    private Vote createOption(Vote vote, StudyVoteRequestDTO.VoteDTO voteDTO) {
        voteDTO.getOptions()
                .forEach(stringOption -> {
                    VoteOption voteOption = VoteOption.builder()
                            .vote(vote)
                            .content(stringOption)
                            .build();
                    voteOption = voteOptionRepository.save(voteOption);
                    vote.addOption(voteOption);
                });
        return voteRepository.save(vote);
    }

    /**
     * 특정 항목에 투표하기 위한 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param voteId 타겟 투표의 아이디를 입력 받습니다.
     * @param votedOptionDTO 회원이 투표한 항목의 아이디 목록을 입력 받습니다.
     * @return 투표 아이디, 회원 아이디, 투표한 항목 목록을 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.VotedOptionDTO vote(Long studyId, Long voteId, StudyVoteRequestDTO.VotedOptionDTO votedOptionDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member loginMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));
        voteRepository.findByIdAndStudyId(voteId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 중복 선택이 허용되지 않는 투표는 여러 개의 option을 선택할 수 없음
        if (!vote.getIsMultipleChoice() && votedOptionDTO.getOptionIdList().size() > 1) {
            throw new StudyHandler(ErrorStatus._STUDY_VOTE_MULTIPLE_CHOICE_NOT_VALID);
        }

        // 한 번 참여한 투표는 다시 참여할 수 없음
        voteOptionRepository.findAllByVoteId(voteId)
                .forEach(option -> {
                    if (voteParticipantRepository.existsByMemberIdAndVoteOptionId(loginMember.getId(), option.getId())) {
                            throw new StudyHandler(ErrorStatus._STUDY_VOTE_RE_PARTICIPATION_INVALID);
                    }
                });

        //=== Feature ===//
        List<VoteParticipant> voteParticipants = votedOptionDTO.getOptionIdList().stream()
                .map(optionId -> {
                    VoteOption votedVoteOption = voteOptionRepository.findById(optionId)
                            .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_OPTION_NOT_FOUND));
                    voteOptionRepository.findByIdAndVoteId(optionId, voteId)
                            .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_OPTION_NOT_FOUND));

                    VoteParticipant voteParticipant = VoteParticipant.builder()
                            .member(loginMember)
                            .voteOption(votedVoteOption)
                            .build();

                    voteParticipant = voteParticipantRepository.save(voteParticipant);
                    loginMember.addMemberVote(voteParticipant);
                    votedVoteOption.addMemberVote(voteParticipant);

                    return voteParticipant;
                })
                .toList();

        return StudyVoteResponseDTO.VotedOptionDTO.toDTO(vote, loginMember, voteParticipants);
    }

    /**
     * 투표를 편집하는 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param voteId 편집할 투표의 아이디를 입력 받습니다.
     * @param voteDTO 편집된 투표의 제목, 항목 목록, 복수 선택 가능 여부, 종료 일시를 입력 받습니다.
     * @return 편집된 투표의 아이디와 제목을 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.VotePreviewDTO updateVote(Long studyId, Long voteId, StudyVoteRequestDTO.VoteUpdateDTO voteDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member loginMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 로그인한 회원이 투표 생성자인지 확인
        if (!loginMember.equals(vote.getMember())) {
            throw new StudyHandler(ErrorStatus._STUDY_VOTE_CREATOR_NOT_AUTHORIZED);
        }

        // 한 명이라도 투표에 참여했으면 투표 편집 불가
        voteOptionRepository.findAllByVoteId(voteId)
                .forEach(option -> {
                    if (voteParticipantRepository.existsByVoteOptionId(option.getId())) {
                        throw new StudyHandler(ErrorStatus._STUDY_VOTE_IS_IN_PROGRESS);
                    }
                });

        //=== Feature ===//
        for (StudyVoteRequestDTO.OptionDTO optionDTO : voteDTO.getOptions()) {
            VoteOption voteOption = voteOptionRepository.findById(optionDTO.getOptionId())
                    .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_OPTION_NOT_FOUND));
            voteOption.setContent(optionDTO.getContent());
            voteOption = voteOptionRepository.save(voteOption);
            vote.updateOption(voteOption);
        }

        vote.updateVote(voteDTO.getTitle(), voteDTO.getIsMultipleChoice(), voteDTO.getFinishedAt());
        vote = voteRepository.save(vote);
        loginMember.updateVote(vote);
        study.updateVote(vote);

        return StudyVoteResponseDTO.VotePreviewDTO.toDTO(vote);
    }

    /**
     * 투표를 삭제하는 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param voteId 삭제할 투표의 아이디를 입력 받습니다.
     * @return 삭제된 투표의 아이디와 제목을 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.VotePreviewDTO deleteVote(Long studyId, Long voteId) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member loginMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));
        voteRepository.findByIdAndStudyId(voteId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 로그인한 회원이 투표 생성자인지 확인
        if (!loginMember.equals(vote.getMember())) {
            throw new StudyHandler(ErrorStatus._STUDY_VOTE_CREATOR_NOT_AUTHORIZED);
        }

        //=== Feature ===//
        deleteOptions(voteId);
        loginMember.deleteVote(vote);
        study.deleteVote(vote);
        voteRepository.delete(vote);

        return StudyVoteResponseDTO.VotePreviewDTO.toDTO(vote);
    }

    /**
     * 모든 투표 항목을 삭제하는 메서드입니다.
     * deleteVote 메서드 내부에서 호출되는 메서드입니다.
     * @param voteId 항목을 삭제할 타겟 투표의 아이디를 입력 받습니다.
     */
    private void deleteOptions(Long voteId) {
        List<VoteOption> voteOptions = voteOptionRepository.findAllByVoteId(voteId);
        voteOptions.forEach(option -> {
            option.deleteAllMemberVotes();
            voteParticipantRepository.deleteAll(voteParticipantRepository.findAllByVoteOptionId(option.getId()));
            voteOptionRepository.delete(option);
        });
    }

    /**
     * 회원이 스터디 장인지 확인합니다.
     * @param memberId 확인 하려는 회원 ID
     * @param studyId 확인 하려는 스터디 ID
     * @return 스터디 장 여부를 반환합니다.
     */
    private boolean isOwner(Long memberId, Long studyId) {
        return studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(memberId, studyId, true).isPresent();
    }

    /**
     * 회원이 스터디 구성원인지 확인합니다.
     * @param memberId 확인 하려는 회원 ID
     * @param studyId 확인 하려는 스터디 ID
     * @return 스터디 참여 여부를 반환합니다.
     */
    private boolean isMember(Long memberId, Long studyId) {
        return studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED).isPresent();
    }

    /**
     * 스터디원을 신고하고 신고 내역을 저장하는 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param memberId 신고할 회원의 아이디를 입력 받습니다.
     * @param studyMemberReportDTO 신고 사유를 입력 받습니다.
     * @return 신고를 당한 회원의 아이디와 이름을 반환합니다.
     */
    @Override
    public MemberResponseDTO.ReportedMemberDTO reportStudyMember(Long studyId, Long memberId, StudyMemberReportDTO studyMemberReportDTO) {

        //=== Exception ===//
        Long reporterId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(reporterId);

        Member reporter = memberRepository.findById(reporterId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(reporterId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 신고당한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 자기 자신을 신고할 수 없음
        if (reporterId.equals(memberId)) {
            throw new StudyHandler(ErrorStatus._STUDY_MEMBER_REPORT_INVALID);
        }


        //=== Feature ===//
        MemberReport memberReport = MemberReport.builder()
                .content(studyMemberReportDTO.getContent())
                .member(member)
                .build();

        memberReport = memberReportRepository.save(memberReport);
        member.addMemberReport(memberReport);

        return MemberResponseDTO.ReportedMemberDTO.toDTO(member);
    }

    /**
     * 스터디 게시글을 신고하고 신고 내역을 저장하는 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력합니다.
     * @param postId 신고할 게시글의 아이디를 입력합니다.
     * @return 신고를 당한 스터디 게시글의 아이디와 제목을 반환합니다.
     */
    @Override
    public StudyPostResDTO.PostPreviewDTO reportStudyPost(Long studyId, Long postId) {

        //=== Exception ===//
        Long reporterId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(reporterId);

        memberRepository.findById(reporterId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Story story = storyRepository.findById(postId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(reporterId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 스터디의 게시글인지 확인
        storyRepository.findByIdAndStudyId(postId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        //=== Feature ===//
        StoryReport storyReport = StoryReport.builder()
                .story(story)
                .build();

        storyReport = storyReportRepository.save(storyReport);
        story.addStudyPostReport(storyReport);

        return StudyPostResDTO.PostPreviewDTO.toDTO(story);
    }

/* ----------------------------- 스터디 To-Do List 관련 API ------------------------------------- */

    /**
     * To-Do List를 생성합니다.
     * @param studyId 생성할 To-Do List가 속한 스터디 ID
     * @param toDoListCreateDTO 생성할 To-Do List 정보
     * @return 생성된 To-Do List 정보
     * @throws StudyHandler 스터디를 찾을 수 없을 때
     * @throws StudyHandler To-Do List를 생성하는 회원이 스터디 회원이 아닐 때
     * @throws StudyHandler 해당 회원을 찾을 수 없을 때
     */
    @Override
    public ToDoListCreateResponseDTO createToDoList(Long studyId,
        ToDoListCreateDTO toDoListCreateDTO) {

        // 스터디 조회
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // To-Do List를 생성하는 회원 ID 조회
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // To-Do List를 생성하는 회원이 스터디 회원인지 확인
        if (!isMember(currentUserId, studyId))
            throw new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND);

        // 회원 조회
        Member member = memberRepository.findById(currentUserId)
            .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // To-Do List 생성
        ToDo toDo = ToDo.builder()
            .study(study)
            .member(member)
            .date(toDoListCreateDTO.getDate())
            .isDone(false)
            .content(toDoListCreateDTO.getContent())
            .build();

        // To-Do List 저장
        toDo.setToDoList();
        toDoRepository.save(toDo);

        // To-Do List 생성 DTO 반환
        return ToDoListCreateResponseDTO.builder()
            .id(toDo.getId())
            .content(toDo.getContent())
            .createdAt(toDo.getCreatedAt())
            .build();
    }

    // studyId가 필요할까?

    /**
     * To-Do List에 작성한 할 일의 체크 상태를 변경 합니다. 체크 상태를 변경 하면 해당 스터디에 참여하고 있는 모든 회원에게 알림이 전송됩니다.
     * @param studyId 스터디 ID
     * @param toDoListId 변경할 To-Do List ID
     * @return To-Do List 변경 여부와 변경 시간
     * @throws StudyHandler To-Do List를 찾을 수 없을 때
     * @throws StudyHandler To-Do List가 스터디에 속하지 않을 때
     * @throws StudyHandler To-Do List를 변경하는 회원이 스터디 회원이 아닐 때
     * @throws StudyHandler 알림 생성 할 스터디 회원을 찾을 수 없을 때
     */
    @Override
    public ToDoListUpdateResponseDTO checkToDoList(Long studyId, Long toDoListId) {

        // To-Do List 조회
        ToDo toDo = toDoRepository.findById(toDoListId)
            .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_TODO_NOT_FOUND));

        // To-Do List가 속한 스터디가 아니면 예외 처리
        if (!Objects.equals(toDo.getStudy().getId(), studyId))
            throw new StudyHandler(ErrorStatus._STUDY_TODO_IS_NOT_BELONG_TO_STUDY);

        // To-Do List를 변경하는 회원이 스터디 회원이 아니면 예외 처리
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!toDo.getMember().getId().equals(currentUserId))
            throw new StudyHandler(ErrorStatus._STUDY_TODO_NOT_AUTHORIZED);

        // To-Do List 체크 상태 변경
        toDo.check();

        // 스터디 회원의 To-Do List 중 하나가 완료 되면, 해당 스터디의 모든 회원에게 알림 전송
        if (toDo.isDone()){
            List<Member> members = studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED).stream()
                .map(StudyMember::getMember)
                .toList();

            // 알림을 생성할 회원이 없으면 알림 생성하지 않음
            if (members.isEmpty()){
                return ToDoListUpdateResponseDTO.builder()
                    .id(toDo.getId())
                    .isDone(toDo.isDone())
                    .updatedAt(toDo.getUpdatedAt())
                    .build();
            }

            // 알림 생성
            members.forEach(studyMember -> {
                Notification notification = Notification.builder()
                    .member(studyMember)
                    .notifierName(toDo.getMember().getName()) // To-Do 완료한 회원 이름
                    .study(toDo.getStudy())
                    .type(NotifyType.TO_DO_UPDATE)
                    .isChecked(Boolean.FALSE)
                    .build();
                notificationRepository.save(notification);
            });
        }

        // To-Do List 저장
        toDoRepository.save(toDo);

        // To-Do List 변경 DTO 반환
        return ToDoListUpdateResponseDTO.builder()
            .id(toDo.getId())
            .isDone(toDo.isDone())
            .updatedAt(toDo.getUpdatedAt())
            .build();
    }


    /**
     * To-Do List 내용을 수정합니다.
     * @param studyId 수정할 To-Do List가 속한 스터디 ID
     * @param toDoListId 수정할 To-Do List ID
     * @param toDoListCreateDTO 수정할 To-Do List 정보
     * @return 수정된 To-Do List 정보
     * @throws StudyHandler To-Do List를 찾을 수 없을 때
     * @throws StudyHandler To-Do List가 스터디에 속하지 않을 때
     * @throws StudyHandler To-Do List를 수정하는 회원이 스터디 회원이 아닐 때
     */
    @Override
    public ToDoListUpdateResponseDTO updateToDoList(Long studyId, Long toDoListId,
        ToDoListCreateDTO toDoListCreateDTO) {

        // To-Do List 조회
        ToDo toDo = toDoRepository.findById(toDoListId)
            .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_TODO_NOT_FOUND));

        // To-Do List가 속한 스터디가 아니면 예외 처리
        if (!Objects.equals(toDo.getStudy().getId(), studyId))
            throw new StudyHandler(ErrorStatus._STUDY_TODO_IS_NOT_BELONG_TO_STUDY);

        // To-Do List를 수정하는 회원이 스터디 회원이 아니면 예외 처리
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!toDo.getMember().getId().equals(currentUserId))
            throw new StudyHandler(ErrorStatus._STUDY_TODO_NOT_AUTHORIZED);

        // To-Do List 수정
        toDo.update(toDoListCreateDTO.getContent(), toDoListCreateDTO.getDate());

        // To-Do List 저장
        toDoRepository.save(toDo);

        // To-Do List 변경 DTO 반환
        return ToDoListUpdateResponseDTO.builder()
            .id(toDo.getId())
            .isDone(toDo.isDone())
            .updatedAt(toDo.getUpdatedAt())
            .build();
    }

    /**
     * To-Do List를 삭제합니다.
     * @param studyId 삭제할 To-Do List가 속한 스터디 ID
     * @param toDoListId 삭제할 To-Do List ID
     * @return 삭제된 To-Do List 정보
     * @throws StudyHandler To-Do List를 찾을 수 없을 때
     * @throws StudyHandler To-Do List가 스터디에 속하지 않을 때
     * @throws StudyHandler To-Do List를 삭제하는 회원이 스터디 회원이 아닐 때
     */
    @Override
    public ToDoListUpdateResponseDTO deleteToDoList(Long studyId, Long toDoListId) {

        // 로그인 중인 회원 ID 조회
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // To-Do List를 삭제하는 회원이 스터디 회원인지 확인
        if (!isMember(currentUserId, studyId))
            throw new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND);

        // To-Do List 조회
        ToDo toDo = toDoRepository.findById(toDoListId)
            .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_TODO_NOT_FOUND));

        // To-Do List가 속한 스터디가 아니면 예외 처리
        if (!Objects.equals(toDo.getStudy().getId(), studyId))
            throw new StudyHandler(ErrorStatus._STUDY_TODO_IS_NOT_BELONG_TO_STUDY);

        // To-Do List를 삭제하는 회원의 ID와 To-Do List를 생성한 회원의 ID가 다르면 예외 처리
        if (!toDo.getMember().getId().equals(currentUserId))
            throw new StudyHandler(ErrorStatus._STUDY_TODO_NOT_AUTHORIZED);

        // To-Do List 삭제
        toDoRepository.deleteById(toDoListId);

        // To-Do List 삭제 DTO 반환
        return ToDoListUpdateResponseDTO.builder()
            .id(toDo.getId())
            .isDone(toDo.isDone())
            .updatedAt(toDo.getUpdatedAt())
            .build();
    }

}
