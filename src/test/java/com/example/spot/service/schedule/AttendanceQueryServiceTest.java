package com.example.spot.service.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.enums.Gender;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.schedule.application.ScheduleQueryServiceImpl;
import com.example.spot.schedule.domain.Schedule;
import com.example.spot.schedule.domain.ScheduleRepository;
import com.example.spot.schedule.domain.association.Quiz;
import com.example.spot.schedule.domain.association.QuizSubmission;
import com.example.spot.schedule.domain.repository.QuizRepository;
import com.example.spot.schedule.domain.repository.QuizSubmissionRepository;
import com.example.spot.schedule.presentation.dto.response.QuizResponseDTO;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AttendanceQueryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StudyRepository studyRepository;
    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private QuizSubmissionRepository quizSubmissionRepository;

    @InjectMocks
    private ScheduleQueryServiceImpl scheduleQueryService;

    private static Study study;
    private static Member member1;
    private static Member member2;
    private static Member owner;
    private static StudyMember member1Study;
    private static StudyMember ownerStudy;
    private static Schedule schedule;
    private static Quiz quiz;
    private static QuizSubmission member1Attendance;
    private static QuizSubmission ownerAttendance;

    private static final LocalDate date = LocalDate.now();

    @BeforeEach
    void setUp() {
        initMember();
        initStudy();
        initMemberStudy();
        initSchedule();
        initMemberAttendance();
        initQuiz();

        when(memberRepository.findById(member1.getId())).thenReturn(Optional.of(member1));
        when(memberRepository.findById(member2.getId())).thenReturn(Optional.of(member2));
        when(memberRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member1.getId(), 1L,
                StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(member1Study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(owner.getId(), 1L,
                StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(ownerStudy));
        when(studyMemberRepository.findAllByStudyIdAndStatus(1L, StudyApplicationStatus.APPROVED))
                .thenReturn(List.of(member1Study, ownerStudy));

        when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));
        when(quizRepository.findAllByScheduleIdAndCreatedAtBetween(schedule.getId(), date.atStartOfDay(),
                date.atStartOfDay().plusDays(1)))
                .thenReturn(List.of(quiz));
        when(quizSubmissionRepository.findByQuizIdAndMemberId(quiz.getId(), member1.getId()))
                .thenReturn(List.of(member1Attendance));
        when(quizSubmissionRepository.findByQuizIdAndMemberId(quiz.getId(), owner.getId()))
                .thenReturn(List.of(ownerAttendance));
    }

    @Test
    @DisplayName("회원 출석부 불러오기 - (성공)")
    void getAllAttendances_Success() {

        // given
        Long studyId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(member1.getId());

        // when
        QuizResponseDTO.AttendanceListDTO result = scheduleQueryService.getAllAttendances(studyId, schedule.getId(),
                date);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getScheduleId()).isEqualTo(schedule.getId());
        assertThat(result.getQuizId()).isEqualTo(quiz.getId());
        assertThat(result.getStudyMembers()).size().isEqualTo(2); // 전체 인원 2명
        assertThat(result.getStudyMembers().stream()
                .filter(QuizResponseDTO.AttendingMemberDTO::getIsAttending)
                .toList()).size().isEqualTo(1); // 출석 인원 1명
    }

    @Test
    @DisplayName("회원 출석부 불러오기 - 스터디가 존재하지 않는 경우 (실패)")
    void getAllAttendances_StudyNotFound_Fail() {

        // given
        Long studyId = 2L;

        // 사용자 인증 정보 생성
        getAuthentication(member2.getId());

        // when & then
        assertThrows(StudyHandler.class, () -> scheduleQueryService.getAllAttendances(studyId, schedule.getId(), date));
    }

    @Test
    @DisplayName("회원 출석부 불러오기 - 스터디 회원이 아닌 경우 (실패)")
    void getAllAttendances_NotStudyMember_Fail() {

        // given
        Long studyId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(member2.getId());

        // when & then
        assertThrows(StudyHandler.class, () -> scheduleQueryService.getAllAttendances(studyId, schedule.getId(), date));
    }

    @Test
    @DisplayName("회원 출석부 불러오기 - 일정이 존재하지 않는 경우 (실패)")
    void getAllAttendances_ScheduleNotFound_Fail() {

        // given
        Long studyId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(member1.getId());

        // when & then
        assertThrows(StudyHandler.class, () -> scheduleQueryService.getAllAttendances(studyId, 2L, date));
    }

    @Test
    @DisplayName("출석 퀴즈 불러오기 - (성공)")
    void getAttendanceQuiz_Success() {

        // given
        Long studyId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(member1.getId());

        // when
        QuizResponseDTO.QuestionDTO result = scheduleQueryService.getAttendanceQuiz(studyId, schedule.getId(), date);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getQuizId()).isEqualTo(quiz.getId());
        assertThat(result.getQuestion()).isEqualTo("최고의 스터디 앱은?");
    }

    @Test
    @DisplayName("출석 퀴즈 불러오기 - 스터디가 존재하지 않는 경우 (실패)")
    void getAttendanceQuiz_StudyNotFound_Fail() {

        // given
        Long studyId = 2L;

        // 사용자 인증 정보 생성
        getAuthentication(member1.getId());

        // when
        assertThrows(StudyHandler.class, () -> scheduleQueryService.getAttendanceQuiz(studyId, schedule.getId(), date));
    }

    @Test
    @DisplayName("출석 퀴즈 불러오기 - 스터디 회원이 아닌 경우 (실패)")
    void getAttendanceQuiz_NotStudyMember_Fail() {

        // given
        Long studyId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(member2.getId());

        // when
        assertThrows(StudyHandler.class, () -> scheduleQueryService.getAttendanceQuiz(studyId, schedule.getId(), date));
    }

    @Test
    @DisplayName("출석 퀴즈 불러오기 - 일정이 존재하지 않는 경우 (실패)")
    void getAttendanceQuiz_ScheduleNotFound_Fail() {

        // given
        Long studyId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(member1.getId());

        // when
        assertThrows(StudyHandler.class, () -> scheduleQueryService.getAttendanceQuiz(studyId, 2L, date));
    }


    /*-------------------------------------------------------- Utils ------------------------------------------------------------------------*/

    private static void initMember() {
        member1 = Member.builder()
                .id(1L)
                .build();
        member2 = Member.builder()
                .id(2L)
                .build();
        owner = Member.builder()
                .id(3L)
                .build();
    }

    private static void initStudy() {
        study = Study.builder()
                .gender(Gender.MALE)
                .minAge(20)
                .maxAge(29)
                .fee(10000)
                .profileImage("a.jpg")
                .hasFee(true)
                .isOnline(true)
                .goal("SQLD")
                .introduction("SQLD 자격증 스터디")
                .title("SQLD Master")
                .maxPeople(10L)
                .build();
    }

    private static void initMemberStudy() {
        ownerStudy = StudyMember.builder()
                .id(1L)
                .status(StudyApplicationStatus.APPROVED)
                .isOwned(true)
                .introduction("Hi")
                .member(owner)
                .study(study)
                .build();
        member1Study = StudyMember.builder()
                .id(2L)
                .status(StudyApplicationStatus.APPROVED)
                .isOwned(false)
                .introduction("Hi")
                .member(member1)
                .study(study)
                .build();
    }

    private static void initSchedule() {
        schedule = Schedule.builder()
                .id(1L)
                .study(study)
                .member(owner)
                .build();
        study.addSchedule(schedule);
    }

    private static void initQuiz() {
        quiz = Quiz.builder()
                .schedule(schedule)
                .member(owner)
                .question("최고의 스터디 앱은?")
                .answer("SPOT")
                .build();
        quiz.addMemberAttendance(member1Attendance);
        quiz.addMemberAttendance(ownerAttendance);
    }

    private static void initMemberAttendance() {
        member1Attendance = QuizSubmission.builder()
                .isCorrect(true)
                .build();
        member1Attendance.setMember(member1);

        ownerAttendance = QuizSubmission.builder()
                .isCorrect(false)
                .build();
        ownerAttendance.setQuiz(quiz);
    }

    private static void getAuthentication(Long memberId) {
        String idString = String.valueOf(memberId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(idString, null,
                Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}