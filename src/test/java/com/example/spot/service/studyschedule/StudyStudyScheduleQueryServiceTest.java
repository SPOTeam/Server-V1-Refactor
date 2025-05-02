package com.example.spot.service.studyschedule;

import com.example.spot.refactor.common.api.exception.handler.StudyHandler;
import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.study.domain.aggregate.studyschedule.StudySchedule;
import com.example.spot.refactor.study.domain.enums.StudyApplicationStatus;
import com.example.spot.refactor.member.domain.enums.Gender;
import com.example.spot.refactor.study.domain.enums.StudySchedulePeriod;
import com.example.spot.refactor.study.domain.aggregate.studymember.StudyMember;
import com.example.spot.refactor.study.domain.aggregate.Study;
import com.example.spot.refactor.member.domain.MemberRepository;
import com.example.spot.refactor.study.domain.aggregate.studymember.StudyMemberRepository;
import com.example.spot.refactor.study.domain.aggregate.studyschedule.StudyScheduleRepository;
import com.example.spot.refactor.study.domain.repository.StudyRepository;
import com.example.spot.refactor.study.application.MemberStudyQueryServiceImpl;
import com.example.spot.refactor.study.presentation.dto.response.ScheduleResponseDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StudyStudyScheduleQueryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StudyRepository studyRepository;
    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private StudyScheduleRepository studyScheduleRepository;

    @InjectMocks
    private MemberStudyQueryServiceImpl memberStudyQueryService;

    private static Study study1;
    private static Study study2;
    private static Member member1;
    private static Member member2;
    private static Member owner;
    private static StudyMember member1Study1;
    private static StudyMember ownerStudy1;
    private static StudySchedule studySchedule1;
    private static StudySchedule studySchedule2;
    private static StudySchedule studySchedule3;

    @BeforeEach
    void setUp() {
        initMember();
        initStudy();
        initMemberStudy();
        initSchedule();

        when(memberRepository.findById(member1.getId())).thenReturn(Optional.of(member1));
        when(memberRepository.findById(member2.getId())).thenReturn(Optional.of(member2));
        when(memberRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study1));
        when(studyRepository.findById(2L)).thenReturn(Optional.of(study2));

        when(studyMemberRepository.findById(member1Study1.getId())).thenReturn(Optional.of(member1Study1));
        when(studyMemberRepository.findById(ownerStudy1.getId())).thenReturn(Optional.of(ownerStudy1));
        when(studyMemberRepository.existsByMemberIdAndStudyIdAndStatus(member1.getId(), study1.getId(), StudyApplicationStatus.APPROVED)).thenReturn(true);
        when(studyMemberRepository.existsByMemberIdAndStudyIdAndStatus(member2.getId(), study1.getId(), StudyApplicationStatus.APPROVED)).thenReturn(false);
        when(studyMemberRepository.existsByMemberIdAndStudyIdAndStatus(owner.getId(), study1.getId(), StudyApplicationStatus.APPROVED)).thenReturn(true);

        when(studyScheduleRepository.findById(studySchedule1.getId())).thenReturn(Optional.of(studySchedule1));
        when(studyScheduleRepository.findById(studySchedule2.getId())).thenReturn(Optional.of(studySchedule2));
        when(studyScheduleRepository.findById(studySchedule3.getId())).thenReturn(Optional.of(studySchedule3));
        when(studyScheduleRepository.findByIdAndStudyId(studySchedule1.getId(), 1L)).thenReturn(Optional.of(studySchedule1));
        when(studyScheduleRepository.findByIdAndStudyId(studySchedule2.getId(), 1L)).thenReturn(Optional.of(studySchedule2));
        when(studyScheduleRepository.findByIdAndStudyId(studySchedule3.getId(), 1L)).thenReturn(Optional.of(studySchedule3));
    }

    @Test
    @DisplayName("스터디 월별 일정 조회 - (성공)")
    void getMonthlySchedules_Success() {

        // given
        Long memberId = 1L; // 로그인 회원
        Long studyId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(memberId);

        // when
        ScheduleResponseDTO.MonthlyScheduleListDTO result = memberStudyQueryService.getMonthlySchedules(studyId, 2024, 1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getScheduleList()).isNotEmpty();
        assertThat(result.getScheduleList()).size().isEqualTo(2);
        assertThat(result.getScheduleList().get(0).getTitle()).isEqualTo(studySchedule1.getTitle());
        assertThat(result.getScheduleList().get(1).getTitle()).isEqualTo(studySchedule2.getTitle());
    }

    @Test
    @DisplayName("스터디 월별 일정 조회 - 스터디 회원이 아닌 경우 (성공)")
    void getMonthlySchedules_NotStudyMember_Fail() {

        // given
        Long memberId = 2L; // 로그인 회원 (스터디 회원 X)
        Long studyId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(memberId);

        // when
        ScheduleResponseDTO.MonthlyScheduleListDTO result = memberStudyQueryService.getMonthlySchedules(studyId, 2024, 1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getScheduleList()).isNotEmpty();
        assertThat(result.getScheduleList()).size().isEqualTo(2);
        assertThat(result.getScheduleList().get(0).getLocation()).isEqualTo("공개되지 않습니다.");
        assertThat(result.getScheduleList().get(1).getLocation()).isEqualTo("공개되지 않습니다.");
    }

    @Test
    @DisplayName("스터디 일정 조회 - (성공)")
    void getSchedule_Success() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long scheduleId = 1L;

        // 사용자 인증 정보 생성
        getAuthentication(memberId);

        // when
        ScheduleResponseDTO.MonthlyScheduleDTO result = memberStudyQueryService.getSchedule(scheduleId, studyId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(studySchedule1.getTitle());
    }

    @Test
    @DisplayName("스터디 일정 조회 - 해당 스터디 일정이 아닌 경우(실패)")
    void getSchedule_NotStudySchedule_Fail() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long scheduleId = 3L;

        // 사용자 인증 정보 생성
        getAuthentication(memberId);

        // when & then
        assertThrows(StudyHandler.class, () -> memberStudyQueryService.getSchedule(scheduleId, studyId));
    }

/*-------------------------------------------------------- Utils ------------------------------------------------------------------------*/

    private static void initMember() {
        member1 = Member.builder()
                .id(1L)
                .studyScheduleList(new ArrayList<>())
                .build();
        member2 = Member.builder()
                .id(2L)
                .studyScheduleList(new ArrayList<>())
                .build();
        owner = Member.builder()
                .id(3L)
                .studyScheduleList(new ArrayList<>())
                .build();
    }

    private static void initStudy() {
        study1 = Study.builder()
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
        study2 = Study.builder()
                .gender(Gender.FEMALE)
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
        ownerStudy1 = StudyMember.builder()
                .id(1L)
                .status(StudyApplicationStatus.APPROVED)
                .isOwned(true)
                .introduction("Hi")
                .member(owner)
                .study(study1)
                .build();
        member1Study1 = StudyMember.builder()
                .id(2L)
                .status(StudyApplicationStatus.APPROVED)
                .isOwned(false)
                .introduction("Hi")
                .member(member1)
                .study(study1)
                .build();
    }

    private static void initSchedule() {

        LocalDateTime startedAt = LocalDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.MIN);

        studySchedule1 = StudySchedule.builder()
                .id(1L)
                .study(study1)
                .member(owner)
                .title("일정1")
                .startedAt(startedAt)
                .finishedAt(startedAt.plusHours(1))
                .studySchedulePeriod(StudySchedulePeriod.NONE)
                .build();
        study1.addSchedule(studySchedule1);
        owner.addSchedule(studySchedule1);

        studySchedule2 = StudySchedule.builder()
                .id(2L)
                .study(study1)
                .member(member1)
                .title("일정2")
                .startedAt(startedAt)
                .finishedAt(startedAt.plusHours(2))
                .studySchedulePeriod(StudySchedulePeriod.MONTHLY)
                .build();
        study1.addSchedule(studySchedule2);
        member1.addSchedule(studySchedule2);

        studySchedule3 = StudySchedule.builder()
                .id(3L)
                .study(study2)
                .member(member2)
                .title("일정3")
                .startedAt(startedAt)
                .finishedAt(startedAt.plusHours(2))
                .studySchedulePeriod(StudySchedulePeriod.WEEKLY)
                .build();
        study2.addSchedule(studySchedule3);
        member2.addSchedule(studySchedule3);
    }

    private static void getAuthentication(Long memberId) {
        String idString = String.valueOf(memberId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(idString, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}