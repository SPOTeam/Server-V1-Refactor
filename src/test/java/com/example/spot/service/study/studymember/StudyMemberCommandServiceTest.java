package com.example.spot.service.study.studymember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.enums.Status;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.study.application.StudyMemberCommandServiceImpl;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.study.presentation.dto.response.StudyResponseDTO;
import com.example.spot.todo.presentation.dto.request.ToDoListRequestDTO;
import java.time.LocalDate;
import java.util.Collections;
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
public class StudyMemberCommandServiceTest {

    @InjectMocks
    private StudyMemberCommandServiceImpl studyMemberCommandService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Study study;

    @Mock
    private Member member;

    @Mock
    private StudyMember studyMember;

    private ToDoListRequestDTO.ToDoListCreateDTO requestDTO;

    @BeforeEach
    void init() {
        requestDTO = ToDoListRequestDTO.ToDoListCreateDTO.builder()
                .content("test")
                .date(LocalDate.EPOCH)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("스터디 종료 - (성공)")
    void terminateStudy_Success() {

        // given
        member = Member.builder()
                .id(1L)
                .name("회원1")
                .build();
        study = Study.builder()
                .id(1L)
                .title("스터디")
                .status(Status.ON)
                .build();
        studyMember = StudyMember.builder()
                .member(member)
                .study(study)
                .isOwned(true)
                .status(StudyApplicationStatus.APPROVED)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(studyMember));

        // when
        StudyResponseDTO.TerminationDTO result = studyMemberCommandService.terminateStudy(1L, "스터디 성과");

        // then
        assertNotNull(result);
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getStudyName()).isEqualTo("스터디");
        assertThat(result.getStatus()).isEqualTo(Status.OFF);
    }

    @Test
    @DisplayName("스터디 종료 - 스터디장이 아닌 경우 (실패)")
    void terminateStudy_NotStudyOwner_Fail() {

        // given
        member = Member.builder()
                .id(1L)
                .name("회원1")
                .build();
        study = Study.builder()
                .id(1L)
                .title("스터디")
                .status(Status.ON)
                .build();
        studyMember = StudyMember.builder()
                .member(member)
                .study(study)
                .isOwned(false)
                .status(StudyApplicationStatus.APPROVED)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(studyMember));

        // when & then
        assertThrows(StudyHandler.class, () -> studyMemberCommandService.terminateStudy(1L, "스터디 성과"));
    }

    @Test
    @DisplayName("스터디 종료 - 진행중인 스터디가 아닌 경우 (실패)")
    void terminateStudy_AlreadyTerminated_Fail() {

        // given
        member = Member.builder()
                .id(1L)
                .name("회원1")
                .build();
        study = Study.builder()
                .id(1L)
                .title("스터디")
                .status(Status.OFF)
                .build();
        studyMember = StudyMember.builder()
                .member(member)
                .study(study)
                .isOwned(false)
                .status(StudyApplicationStatus.APPROVED)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(studyMember));

        // when & then
        assertThrows(StudyHandler.class, () -> studyMemberCommandService.terminateStudy(1L, "스터디 성과"));
    }


}
