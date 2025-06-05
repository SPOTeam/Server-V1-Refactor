package com.example.spot.service.study.studymember;

import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.member.domain.Member;
import com.example.spot.schedule.domain.ScheduleRepository;
import com.example.spot.story.domain.StoryRepository;
import com.example.spot.study.application.StudyMemberQueryServiceImpl;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.todo.domain.ToDo;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StudyMemberQueryServiceTest {

    @InjectMocks
    private StudyMemberQueryServiceImpl studyMemberQueryService;

    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private StoryRepository storyRepository;
    @Mock
    private ScheduleRepository scheduleRepository;

    private static Member member;
    private static Member member2;
    private static Study study;
    private static StudyMember studyMember;
    private static StudyMember studyMember2;
    private static StudyMember apply;
    private static ToDo toDo;
    
    @BeforeEach
    void setup(){
        member = Member.builder()
                .id(1L)
                .build();
        member2 = Member.builder()
                .id(2L)
                .build();
        study = Study.builder()
                .build();
        studyMember = StudyMember.builder()
                .introduction("title").study(study).member(member).isOwned(true).status(StudyApplicationStatus.APPROVED).build();
        apply = StudyMember.builder()
                .introduction("title").study(study).member(member).isOwned(false).status(StudyApplicationStatus.APPLIED).build();
        studyMember2 = StudyMember.builder()
                .introduction("title").study(study).member(member2).isOwned(true).status(StudyApplicationStatus.APPROVED).build();
        toDo = ToDo.builder()
                .id(1L)
                .build();

        Long studyId = 1L;

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.ofNullable(studyMember));
        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        // SecurityContext 생성 및 설정
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /* ------------------------------------------------ 특정 스터디 회원 목록 전체 조회  --------------------------------------------------- */

    @Test
    @DisplayName("특정 스터디 회원 목록 조회 - 성공")
    void 특정_스터디_회원_목록_조회_성공() {

        // given
        Long studyId = 1L;
        when(studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED)).thenReturn(List.of(studyMember));

        // when
        StudyMemberResponseDTO.StudyMemberListDTO responseDTO = studyMemberQueryService.findStudyMembers(studyId);

        // then
        assertEquals(1, responseDTO.getTotalElements());
        assertEquals(1L, responseDTO.getMembers().get(0).getMemberId());

    }

    @Test
    @DisplayName("특정 스터디 회원 목록 조회 - 스터디에 멤버가 존재하지 않는 경우")
    void 특정_스터디_회원_목록_조회_실패() {

        // given
        Long studyId = 1L;
        when(studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED)).thenReturn(List.of());

        // when & then
        assertThrows(GeneralException.class, () -> studyMemberQueryService.findStudyMembers(studyId));
    }


    /* ------------------------------------------------ 모집중인 스터디에 신청한 회원 목록 조회  --------------------------------------------------- */

    @Test
    @DisplayName("모집중인 스터디에 신청한 회원 목록 조회 - 성공")
    void 스터디_신청_회원_목록_조회_성공() {

        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 1L, true)).thenReturn(
                Optional.ofNullable(studyMember));
        when(studyMemberRepository.findAllByStudyIdAndStatus(1L, StudyApplicationStatus.APPLIED))
                .thenReturn(List.of(apply));

        // when
        StudyMemberResponseDTO.StudyMemberListDTO responseDTO = studyMemberQueryService.findStudyApplicants(1L);

        // then
        assertEquals(1, responseDTO.getTotalElements());
        assertEquals(1L, responseDTO.getMembers().get(0).getMemberId());
    }

    @Test
    @DisplayName("모집중인 스터디에 신청한 회원 목록 조회 - 로그인 한 회원이 해당 스터디 장이 아닌 경우")
    void 스터디_신청_회원_목록_조회_실패_1() {

        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 1L, true)).thenReturn(
                Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> studyMemberQueryService.findStudyApplicants(1L));
    }

    @Test
    @DisplayName("모집중인 스터디에 신청한 회원 목록 조회 - 스터디 신청자가 존재하지 않는 경우")
    void 스터디_신청_회원_목록_조회_실패_2() {

        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 1L, true)).thenReturn(
                Optional.ofNullable(studyMember));
        when(studyMemberRepository.findAllByStudyIdAndStatus(1L, StudyApplicationStatus.APPLIED))
                .thenReturn(List.of());

        // when & then
        assertThrows(GeneralException.class, () -> studyMemberQueryService.findStudyApplicants(1L));
    }


    /* ------------------------------------------------ 스터디 신청자 정보 조회  --------------------------------------------------- */

    @Test
    @DisplayName("스터디 신청자 정보 조회 - 성공")
    void 스터디_신청자_정보_조회_성공() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 100L, true)).thenReturn(
                Optional.ofNullable(studyMember));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPLIED))
                .thenReturn(Optional.ofNullable(apply));

        // when
        StudyMemberResponseDTO.ApplyingMemberDTO responseDTO = studyMemberQueryService.findStudyApplication(100L, 1L);

        // then
        assertEquals(1L, responseDTO.getMemberId());
    }

    @Test
    @DisplayName("스터디 신청자 정보 조회 - 로그인 한 회원이 스터디 장이 아닌 경우")
    void 스터디_신청자_정보_조회_실패_1() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 100L, true)).thenReturn(
                Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> studyMemberQueryService.findStudyApplication(100L, 1L));
    }

    @Test
    @DisplayName("스터디 신청자 정보 조회 - 스터디 신청자가 없는 경우 ")
    void 스터디_신청자_정보_조회_실패_2() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 100L, true)).thenReturn(
                Optional.ofNullable(studyMember));

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPLIED))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> studyMemberQueryService.findStudyApplication(100L, 1L));
    }

    @Test
    @DisplayName("스터디 신청자 정보 조회 - 스터디 신청자가 스터디 장인 경우")
    void 스터디_신청자_정보_조회_실패_3() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 100L, true)).thenReturn(
                Optional.ofNullable(studyMember));

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPLIED))
                .thenReturn(Optional.ofNullable(studyMember));

        // when & then
        assertThrows(GeneralException.class, () -> studyMemberQueryService.findStudyApplication(100L, 1L));
    }


    /* ------------------------------------------------ 스터디 신청 여부 조회  --------------------------------------------------- */

    @Test
    @DisplayName("스터디 신청 여부 조회 - 성공")
    void 스터디_신청_여부_조회_성공() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.empty());
        when(studyMemberRepository.existsByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPLIED))
                .thenReturn(true);

        // when
        StudyMemberResponseDTO.AppliedStudyDTO responseDTO = studyMemberQueryService.isApplied(100L);

        // then
        assertEquals(100L, responseDTO.getStudyId());
        assertEquals(true, responseDTO.isApplied());
    }

    @Test
    @DisplayName("스터디 신청 여부 조회 - 이미 가입 된 경우")
    void 스터디_신청_여부_조회_실패() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        // when & then
        assertThrows(GeneralException.class, () -> studyMemberQueryService.isApplied(100L));
    }
}
