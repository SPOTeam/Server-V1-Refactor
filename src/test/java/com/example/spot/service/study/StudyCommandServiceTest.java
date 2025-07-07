package com.example.spot.service.study;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.member.domain.enums.Gender;
import com.example.spot.study.application.StudyCommandServiceImpl;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.association.Region;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.association.StudyRegion;
import com.example.spot.study.domain.association.StudyTheme;
import com.example.spot.study.domain.association.Theme;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.enums.StudyState;
import com.example.spot.study.domain.enums.ThemeType;
import com.example.spot.study.domain.repository.RegionRepository;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.study.domain.repository.StudyRegionRepository;
import com.example.spot.study.domain.repository.StudyThemeRepository;
import com.example.spot.study.domain.repository.ThemeRepository;
import com.example.spot.study.presentation.dto.request.StudyMemberRequestDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyResponseDTO;
import java.util.ArrayList;
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
class StudyCommandServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StudyRepository studyRepository;
    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private RegionRepository regionRepository;
    @Mock
    private StudyRegionRepository studyRegionRepository;

    @Mock
    private ThemeRepository themeRepository;
    @Mock
    private StudyThemeRepository studyThemeRepository;

    @InjectMocks
    private StudyCommandServiceImpl studyCommandService;

    private static Study study;
    private static Member member1;
    private static Member member2;
    private static Member owner;
    private static StudyMember member1Study;
    private static StudyMember ownerStudy;
    private static Region region;
    private static Theme theme;

    @BeforeEach
    void setUp() {
        initMember();
        initStudy();
        initMemberStudy();
        initRegion();
        initTheme();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(memberRepository.findById(3L)).thenReturn(Optional.of(owner));

        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(regionRepository.findByCode("123456")).thenReturn(Optional.of(region));
        when(themeRepository.findByThemeType(ThemeType.자격증)).thenReturn(Optional.of(theme));

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(member1Study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(3L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(ownerStudy));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(3L, 1L, true))
                .thenReturn(Optional.of(ownerStudy));
    }

    @Test
    @DisplayName("스터디 신청 - (성공)")
    void applyToStudy_Success() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;

        getAuthentication(memberId);

        StudyMemberRequestDTO.JoinDTO joinDTO = StudyMemberRequestDTO.JoinDTO.builder()
                .introduction("Hi")
                .build();

        StudyMember studyMember = StudyMember.builder()
                .id(3L)
                .member(member2)
                .study(study)
                .status(StudyApplicationStatus.APPLIED)
                .isOwned(false)
                .introduction(joinDTO.getIntroduction())
                .build();

        when(studyMemberRepository.countByStatusAndStudyId(StudyApplicationStatus.APPROVED, studyId))
                .thenReturn(2L);
        when(studyMemberRepository.findByMemberIdAndStatusNot(memberId, StudyApplicationStatus.REJECTED))
                .thenReturn(List.of());
        when(studyMemberRepository.save(any(StudyMember.class)))
                .thenReturn(studyMember);

        // when
        StudyMemberResponseDTO.JoinDTO result = studyCommandService.applyToStudy(studyId, joinDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(memberId);
        verify(studyMemberRepository, times(1)).save(any(StudyMember.class));
    }

    @Test
    @DisplayName("스터디 신청 - 이미 스터디에 신청했거나 스터디 회원인 경우(실패)")
    void applyToStudy_StudyMember_Fail() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;

        getAuthentication(memberId);

        StudyMemberRequestDTO.JoinDTO joinDTO = StudyMemberRequestDTO.JoinDTO.builder()
                .introduction("Hi")
                .build();

        StudyMember studyMember = StudyMember.builder()
                .id(3L)
                .member(member1)
                .study(study)
                .status(StudyApplicationStatus.APPLIED)
                .isOwned(false)
                .introduction(joinDTO.getIntroduction())
                .build();

        when(studyMemberRepository.countByStatusAndStudyId(StudyApplicationStatus.APPROVED, studyId))
                .thenReturn(2L);
        when(studyMemberRepository.findByMemberIdAndStatusNot(memberId, StudyApplicationStatus.REJECTED))
                .thenReturn(List.of(member1Study));
        when(studyMemberRepository.save(any(StudyMember.class)))
                .thenReturn(studyMember);

        // when & then
        assertThrows(StudyHandler.class, () -> studyCommandService.applyToStudy(studyId, joinDTO));
    }

    @Test
    @DisplayName("스터디 신청 - 모집중인 스터디가 아닌 경우(실패)")
    void applyToStudy_NotRecruitingStudy_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 2L;

        getAuthentication(memberId);

        Study study = Study.builder()
                .title("마감된 스터디")
                .maxPeople(1L)
                .build();

        StudyMemberRequestDTO.JoinDTO joinDTO = StudyMemberRequestDTO.JoinDTO.builder()
                .introduction("Hi")
                .build();

        StudyMember studyMember = StudyMember.builder()
                .id(3L)
                .member(member2)
                .study(study)
                .status(StudyApplicationStatus.APPLIED)
                .isOwned(false)
                .introduction(joinDTO.getIntroduction())
                .build();

        when(studyMemberRepository.countByStatusAndStudyId(StudyApplicationStatus.APPROVED, studyId))
                .thenReturn(1L);
        when(studyMemberRepository.findByMemberIdAndStatusNot(memberId, StudyApplicationStatus.REJECTED))
                .thenReturn(List.of());
        when(studyMemberRepository.save(any(StudyMember.class)))
                .thenReturn(studyMember);

        // when & then
        assertThrows(StudyHandler.class, () -> studyCommandService.applyToStudy(studyId, joinDTO));
    }

    @Test
    @DisplayName("스터디 등록 - (성공)")
    void registerStudy() {

        // given
        Long memberId = 1L;

        getAuthentication(memberId);

        StudyMemberRequestDTO.RegisterDTO registerDTO = StudyMemberRequestDTO.RegisterDTO.builder()
                .themes(List.of(ThemeType.자격증))
                .title("새로운 스터디")
                .goal("목표")
                .introduction("소개")
                .isOnline(false)
                .profileImage("profileImage")
                .regions(List.of("123456"))
                .maxPeople(5L)
                .gender(Gender.UNKNOWN)
                .minAge(1)
                .maxAge(100)
                .fee(0)
                .hasFee(false)
                .build();

        Study study = Study.builder()
                .title("새로운 스터디")
                .maxPeople(10L)
                .build();

        StudyMember studyMember = StudyMember.builder()
                .member(member1)
                .study(study)
                .build();

        StudyRegion studyRegion = StudyRegion.builder()
                .region(region)
                .study(study)
                .build();

        StudyTheme studyTheme = StudyTheme.builder()
                .theme(theme)
                .study(study)
                .build();

        when(studyRepository.save(any(Study.class))).thenReturn(study);
        when(studyMemberRepository.save(any(StudyMember.class))).thenReturn(studyMember);
        when(studyRegionRepository.save(any(StudyRegion.class))).thenReturn(studyRegion);
        when(studyThemeRepository.save(any(StudyTheme.class))).thenReturn(studyTheme);

        // when
        StudyResponseDTO.RegisterDTO result = studyCommandService.registerStudy(registerDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("새로운 스터디");
        verify(studyRepository, times(2)).save(any(Study.class));
        verify(studyMemberRepository, times(1)).save(any(StudyMember.class));
        verify(studyRegionRepository, times(1)).save(any(StudyRegion.class));
        verify(studyThemeRepository, times(1)).save(any(StudyTheme.class));
    }

    @Test
    void likeStudy() {
    }

    /*-------------------------------------------------------- Utils ------------------------------------------------------------------------*/

    private static void initMember() {
        member1 = Member.builder()
                .id(1L)
                .scheduleList(new ArrayList<>())
                .build();
        member2 = Member.builder()
                .id(2L)
                .scheduleList(new ArrayList<>())
                .build();
        owner = Member.builder()
                .id(3L)
                .scheduleList(new ArrayList<>())
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
                .studyState(StudyState.RECRUITING)
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

    private static void initRegion() {
        region = Region.builder()
                .code("123456")
                .build();
    }

    private static void initTheme() {
        theme = Theme.builder()
                .themeType(ThemeType.자격증)
                .build();
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